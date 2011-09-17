package us.donlab.cookbookreader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.TextView;

public class Reader extends Activity implements OnInitListener {
	private TextToSpeech recipeReader;
	private static final int VOICE_REC_REQ_CODE = 908;
	private static final int TTS_REQ_CODE = 518;
	private int position;
	private ArrayList<String> recipeProcedure;
	//private ArrayList<String> ingredients;
	
	@Override
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);
		recipeProcedure = new ArrayList<String>();
		TextView tv = new TextView(this);
        tv.setText("Has this crashed?");
        setContentView(tv); 
        //test code
		String word1 = new String("step one");
		String word2 = new String("step two");
		String word3 = new String("step three");
		String word4 = new String("step four");
		recipeProcedure.add(word1);
		recipeProcedure.add(word2);
		recipeProcedure.add(word3);
		recipeProcedure.add(word4);
        //end test code
        //check for voice recognition capability
        PackageManager pacman = getPackageManager();
        List<ResolveInfo> activities = pacman.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0){
			tv.setText("Voice Recognition Available"); //TODO change to an actual action
		}
		else{
			tv.setText("Voice Recognition Not Available");
		}
        //Start up the TTS system on activity creation
        Intent checkTTS = new Intent();
		checkTTS.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTS, TTS_REQ_CODE);
	}
	
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS){
			recipeReader.setLanguage(Locale.US);
			startVoiceRec();
		}
	
	}
	
	private void startVoiceRec(){
		Intent intentVoiceRec = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intentVoiceRec.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		startActivityForResult(intentVoiceRec, VOICE_REC_REQ_CODE);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == VOICE_REC_REQ_CODE) {
				if (resultCode == RESULT_OK){
						ArrayList<String> voiceMatches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);			
						checkVoice(voiceMatches);
				}
				else{
					startVoiceRec();
				}
		}
		else if (requestCode == TTS_REQ_CODE){
				if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
					//start TTS
					recipeReader = new TextToSpeech(this, this); 
				}
				else{
					//install TTS
					Intent installTTSIntent = new Intent();
					installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
					startActivity(installTTSIntent);
				}
				
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void checkVoice(ArrayList<String> voiceMatches){ //check for position words, otherwise restart voice rec
		if (voiceMatches.contains((String)"next")){
			sayStep(2);
		}
		else if (voiceMatches.contains((String)"previous")){
			sayStep(0);
		}
		else if (voiceMatches.contains((String)"repeat")){
			sayStep(1);
		}
		
		else if (voiceMatches.contains((String)"ingredients")){	
		
		}
		
		else{
			startVoiceRec();
		}
	}
	
	private void sayStep(int voiceOption){
		if(voiceOption == 1) { //repeat the instruction
			recipeReader.speak(recipeProcedure.get(position),TextToSpeech.QUEUE_ADD, null );
			startVoiceRec();
		}
		
		else if(voiceOption == 0) { //repeat the previous instruction
			if(position > 0){
				position = position - 1;
			}
			else{
				position = 0;
			}
			recipeReader.speak(recipeProcedure.get(position), TextToSpeech.QUEUE_ADD, null);
			startVoiceRec();
		}
		
		else if(voiceOption == 2) { //say the next instruction
			if(position < recipeProcedure.size()){
				position = position + 1;
			}
			recipeReader.speak(recipeProcedure.get(position), TextToSpeech.QUEUE_ADD, null);
			startVoiceRec();
		}	
	}
	
		@Override
		protected void onPause(){
			super.onPause();
		}
		
		@Override
		protected void onDestroy(){
			super.onDestroy();
			recipeReader.shutdown();
		}

}
