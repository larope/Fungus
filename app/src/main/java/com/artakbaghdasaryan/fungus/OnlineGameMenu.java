package com.artakbaghdasaryan.fungus;

import static android.content.ContentValues.TAG;

import static com.artakbaghdasaryan.fungus.CreateClassicGame.isValidInteger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.artakbaghdasaryan.fungus.ChessLogics.CellOnlineData;
import com.artakbaghdasaryan.fungus.ChessLogics.ChessGameDataOnline;
import com.artakbaghdasaryan.fungus.ChessLogics.MoveOnlineData;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.artakbaghdasaryan.fungus.Util.Timer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class OnlineGameMenu extends AppCompatActivity {
    private Dialog _createNewGamePopup;

    private ChessGameDataOnline _newChessGame;

    private EditText _hours;
    private EditText _minutes;
    private EditText _seconds;
    private EditText _increment;
    private String _mode;

    private String[] _modes = {"Classic", "Fungus"};

    AutoCompleteTextView _autoCompleteText;
    ArrayAdapter<String> _arrayItems;
    ArrayAdapter<String> _availableGames;

    private ListView _roomsList;

    FirebaseFirestore _dataBase;

    private static ChessGameDataOnline _selectedGame;

    private Handler _handler;
    private Runnable _runnable;

    private boolean _haveCreatedGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_menu);
        _selectedGame = ChessGameDataOnline.Empty;

        _createNewGamePopup = new Dialog(OnlineGameMenu.this);
        _createNewGamePopup.setContentView(R.layout.create_chess_game_popup);
        _createNewGamePopup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        _arrayItems = new ArrayAdapter<String>(this, R.layout.dropdown_list_item, _modes);
        _availableGames = new ArrayAdapter<String>(this, R.layout.list_item);

        _roomsList = findViewById(R.id.rooms_list);

        _dataBase = FirebaseFirestore.getInstance();

        SetUpUIUpdate();

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _createNewGamePopup.show();

                _hours =_createNewGamePopup.findViewById(R.id.hours);
                _minutes = _createNewGamePopup.findViewById(R.id.minutes);
                _seconds = _createNewGamePopup.findViewById(R.id.seconds);

                _increment = _createNewGamePopup.findViewById(R.id.increment);

                _autoCompleteText = _createNewGamePopup.findViewById(R.id.auto_complete_text);
                _autoCompleteText.setAdapter(_arrayItems);
                _autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        _mode = (String) parent.getItemAtPosition(position);
                    }
                });


                _createNewGamePopup.findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CreateGame()){
                            Intent intent = new Intent(OnlineGameMenu.this, ChessOnlineGame.class);
                            intent.putExtra("mode", _mode);
                            intent.putExtra("duration", _newChessGame.durationInMilliseconds);
                            intent.putExtra("increment", _newChessGame.incrementInMilliseconds);


                            UploadGame(_newChessGame);
                            _mode = null;
                            _createNewGamePopup.dismiss();
                        }
                    }


                });

                _createNewGamePopup.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _createNewGamePopup.dismiss();
                    }
                });


            }
        });

        findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinGame();
            }
        });
        LoadGames();
    }

    private void SetUpUIUpdate() {
        _handler = new Handler();
        _runnable = new Runnable() {
            @Override
            public void run() {
                LoadGames();
                _handler.postDelayed(this, 1000);
            }
        };

        _runnable.run();
    }

    private void DisplayToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    private boolean CreateGame(){
        String hoursStr = _hours.getText().toString();
        String minutesStr = _minutes.getText().toString();
        String secondsStr = _seconds.getText().toString();
        String incrementStr = _increment.getText().toString();




        if (!isValidInteger(hoursStr) || !isValidInteger(minutesStr) || !isValidInteger(secondsStr) || !isValidInteger(incrementStr) || _mode == null) {
            DisplayToast("Please enter valid integers in all fields");
            return false;
        }
        Random random = new Random();

        _newChessGame = new ChessGameDataOnline(
                "none",
                Timer.ConvertToMilliseconds(
                Integer.parseInt(hoursStr), Integer.parseInt(minutesStr),
                Integer.parseInt(secondsStr)),
                Integer.parseInt(incrementStr) * 1000L,
                _mode,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        _newChessGame.isHostWhite = random.nextBoolean();

        return true;
    }

    private void UploadGame(ChessGameDataOnline data){
        if(_haveCreatedGame) {
            DisplayToast("You have already created a game");
            return;
        }

        Map<String, Object> chessGame = new HashMap<>();
        chessGame.put("Mode", data.getMode());
        chessGame.put("Duration", data.getDurationInMilliseconds());
        chessGame.put("Increment", data.getIncrementInMilliseconds());
        chessGame.put("HostId", data.hostId);
        chessGame.put("HostUsername", data.hostname);
        chessGame.put("LastMove", MoveOnlineData.Empty);
        chessGame.put("PlayerWhiteId", null);
        chessGame.put("PlayerBlackId", null);
        chessGame.put("PlayerWhiteName", null);
        chessGame.put("PlayerBlackName", null);
        chessGame.put("IsHostWhite", data.isHostWhite);
        chessGame.put("CurrentPlayerColor", PieceColor.white);
        chessGame.put("LastChangesUserId", null);
        chessGame.put("LastChangedCellFrom", CellOnlineData.Empty);
        chessGame.put("LastChangedCellTo", CellOnlineData.Empty);

        chessGame.put("LastChangedCellFrom2", CellOnlineData.Empty);
        chessGame.put("LastChangedCellTo2", CellOnlineData.Empty);

        Log.d("MALOG", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        _dataBase.collection("games")
                .add(chessGame)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        _haveCreatedGame = true;
                        data.gameId = documentReference.getId();
                        _selectedGame = data;
                        JoinGame();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    private void LoadGames(){
        List<ListItem> listItems = new ArrayList<>();

        _dataBase.collection("games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> chessGame = document.getData();

                                String name = (String) chessGame.get("HostUsername");
                                String mode = (String) chessGame.get("Mode");
                                Long duration = (Long) chessGame.get("Duration");
                                Long increment = (Long) chessGame.get("Increment");
                                String userId = (String) chessGame.get("UserId");
                                String hostId = (String) chessGame.get("HostId");
                                boolean isHostWhite = (boolean) chessGame.get("IsHostWhite");

                                ListItem item = new ListItem(

                                        name,
                                        mode,
                                        duration+"",
                                        increment+"",
                                        userId,
                                        hostId,
                                        isHostWhite
                                );

                                item.gameId = document.getId();

                                listItems.add(item);

                            }

                            CustomAdapter adapter = new CustomAdapter(getBaseContext(), listItems);

                            _roomsList.setAdapter(adapter);
                        } else {
                            Log.w("MALOG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void JoinGame(){
        if(_selectedGame == ChessGameDataOnline.Empty){
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, ChessOnlineGame.class);

        FirebaseFirestore.getInstance().collection("games").document(_selectedGame.gameId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String playerColor;

                        if(_selectedGame.hostId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            playerColor = Boolean.parseBoolean(document.getString("isHostWhite")) ? "white" : "black";
                        }else{
                            playerColor = !Boolean.parseBoolean(document.getString("isHostWhite")) ? "white" : "black";
                        }

                        if(playerColor.equals("white")){
                            _dataBase.collection("games")
                                    .document(_selectedGame.gameId)
                                    .update("PlayerWhiteName",
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                                            );
                            _dataBase.collection("games")
                                    .document(_selectedGame.gameId)
                                    .update("PlayerWhiteId",
                                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                                    );
                        }
                        else if(playerColor.equals("black")){
                            _dataBase.collection("games")
                                    .document(_selectedGame.gameId)
                                    .update("PlayerBlackName",
                                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                                    );
                            _dataBase.collection("games")
                                    .document(_selectedGame.gameId)
                                    .update("PlayerBlackId",
                                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                                    );
                        }


                        intent.putExtra("mode",  (_selectedGame.mode));
                        intent.putExtra("duration",  (_selectedGame.durationInMilliseconds));
                        intent.putExtra("increment",  (_selectedGame.incrementInMilliseconds));
                        intent.putExtra("hostId",  (_selectedGame.hostId));
                        intent.putExtra("hostUsername",  (_selectedGame.hostname));
                        intent.putExtra("gameId",  (_selectedGame.gameId));
                        intent.putExtra("isHostWhite",  (_selectedGame.isHostWhite));
                        intent.putExtra("currentPlayerColor", playerColor);

                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public static void SelectGame(ListItem item){
        _selectedGame = new ChessGameDataOnline(
                item.gameId,
                Long.parseLong(item.getTimeControl()),
                Long.parseLong(item.getIncrement()),
                item.getMode(),
                item.getHostId(),
                item.getHostName()
                );
    }
}


class ListItem {


    private String hostName;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    private String hostId;
    private String mode;
    private String timeControl;
    private String increment;
    public String gameId;
    public boolean isHostWhite;

    public ListItem(String hostName, String mode, String timeControl, String increment, String gameId, String hostId, boolean isHostWhite) {
        this.hostName = hostName;
        this.mode = mode;
        this.timeControl = timeControl;
        this.increment = increment;
        this.gameId = gameId;
        this.hostId = hostId;
        this.isHostWhite = isHostWhite;
    }

    public String getHostName() {
        return hostName;
    }

    public String getMode() {
        return mode;
    }

    public String getTimeControl() {
        return timeControl;
    }


    public String getIncrement() {
        return increment;
    }
}

class CustomAdapter extends ArrayAdapter<ListItem> {
    public CustomAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem listItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView hostNameTextView = convertView.findViewById(R.id.host_name);
        TextView modeTextView = convertView.findViewById(R.id.mode);
        TextView timeControlTextView = convertView.findViewById(R.id.time_control);
        ImageButton joinButton = convertView.findViewById(R.id.join_game);

        hostNameTextView.setText(listItem.getHostName());
        modeTextView.setText("Mode: " + listItem.getMode());
        timeControlTextView.setText("Time: " + listItem.getTimeControl() + "(+"+(Integer.parseInt(listItem.getIncrement())/1000)+")");

        joinButton.setImageDrawable(convertView.getResources().getDrawable(R.drawable.play_icon));
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event
                OnlineGameMenu.SelectGame(listItem);
            }
        });

        return convertView;
    }
}