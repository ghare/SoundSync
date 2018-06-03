package com.mei.daam.soundsync;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;

public class JoinGroupPresenter extends AppCompatActivity {
    private final JoinGroupFragment fragment;
    private final Button nextButton;
    private final EditText editText;
    private final ImageButton qrCodeButton;

    public JoinGroupPresenter(JoinGroupFragment fragment, Button nextButton, EditText editText, ImageButton qrCodeButton) {

        this.fragment = fragment;
        this.nextButton = nextButton;
        this.editText = editText;
        this.qrCodeButton = qrCodeButton;
    }

    public void setListeners() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = editText.getText().toString();
                tryToConnect(groupName);
            }
        });
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(fragment.getActivity());
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("Scan");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                fragment.showToast("You cancelled the scanning!");
            }else{
                tryToConnect(result.getContents());
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void tryToConnect(String groupName){
        nextButton.setEnabled(false);
        fragment.toggleProgressBar();
        if (ConnectionHandler.hasNetworkConnection(fragment.getContext())) {
            Group group = new Group(groupName);
            FireBaseHandler fireBaseHandler = new FireBaseHandler(group);
            fireBaseHandler.checkAndRightGroupOnDB();
            handleFirebaseResponse(fireBaseHandler, group);
        } else {
            fragment.showToast("Network not available");
            nextButton.setEnabled(true);
            fragment.toggleProgressBar();
        }
    }

    private void handleFirebaseResponse(FireBaseHandler fireBaseHandler, Group group) {
        fireBaseHandler.groupExists().doOnNext(exists -> {
            if (exists == ResultMapper.EXISTS) {
                Intent intent = createIntent(group);
                fragment.startActivity(intent);
            } else if (exists == ResultMapper.CREATE) {
                fragment.showToast("This group doesn't exist");
            } else {
                fragment.showToast("An unexpected error occured");
            }
            fragment.toggleProgressBar();
            nextButton.setEnabled(true);
        }).subscribe();
    }

    private Intent createIntent(Group group) {
        Intent intent = new Intent(fragment.getContext(), HostYoutubeActivity.class);
        intent.putExtra(MainActivity.GROUP_NAME, group.getName());
        intent.putExtra("Group", (Serializable) group);
        intent.putExtra("isHost", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }
}
