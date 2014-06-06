package com.dinsfire.ponymotes;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;



public class SearchFragment extends Fragment {
	
	public enum SearchMode {
		NORMAL, RETURN_PICTURE, RETURN_CODE
	}
	
	public SearchMode currentMode;

	private TextView searchResults;
	private EditText searchField;
	private ImageView searchButton;
	private GridView gridView;
	private EmoteAdapter emoteAdapter;
	
	private DatabaseController dbController;
	
	private Prefrences prefs;

	public SearchFragment() {
		setNormalMode();
	}
	
	public void setPickPictureMode() {
		currentMode = SearchMode.RETURN_PICTURE;
	}
	
	public void setPickEmoteCode() {
		currentMode = SearchMode.RETURN_CODE;
	}
	
	public void setNormalMode() {
		currentMode = SearchMode.NORMAL;
	}
	
	public SearchMode getCurrentMode() {
		return currentMode;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		
		prefs = new Prefrences(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		
		//dbController = ((MainActivity)this.getActivity()).getDBController();
		dbController = new DatabaseController(getActivity());
		searchField = (EditText)rootView.findViewById(R.id.searchField);
		searchResults = (TextView)rootView.findViewById(R.id.numOfResults);
		searchButton = (ImageView)rootView.findViewById(R.id.searchButton);
		
		emoteAdapter = new EmoteAdapter(getActivity());
		
		gridView = (GridView)rootView.findViewById(R.id.gridview);
		gridView.setAdapter(emoteAdapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            if(currentMode == SearchMode.RETURN_PICTURE) {
	            	
	            	Intent data = new Intent();
	            	data.setType("image/*");
	            	
	            	if(prefs.altSharingMethod())
	            		data.setData(Uri.fromFile(FileIO.getEmoteByName(emoteAdapter.getItem(position))));
	        	    else
	        	    	data.setData(Uri.fromFile(new File(FileIO.copyTempEmote(emoteAdapter.getItem(position)))));
	        		
					getActivity().setResult(Activity.RESULT_OK, data);
	        		getActivity().finish();
	            
	            } else if(currentMode == SearchMode.RETURN_CODE) {
	            	
	            	Intent data = new Intent();
	            	data.putExtra(Intent.EXTRA_STREAM, "[](/" + emoteAdapter.getItem(position) + ") ");
	            	getActivity().setResult(Activity.RESULT_OK, data);
	        		getActivity().finish();
	        		
	            } else {
	            	
	            	Toast.makeText(getView().getContext(), emoteAdapter.getItem(position) + " copied to clipboard.", Toast.LENGTH_SHORT).show();
		            copyEmoteToClipboard(emoteAdapter.getItem(position));
	            
	            }
	        	
	        	
	            
	        }
	    });
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				if(currentMode == SearchMode.NORMAL) {
					shareEmote(emoteAdapter.getItem(position));
				}
				
	        	return true;
	        }
			
		});
		
		searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					search(searchField.getText().toString());
		            return true;
		        }
		        return false;
			}
		});
		
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				search(searchField.getText().toString());
			}	
		});
		
		

		
		return rootView;
	}
	
	
	
	public void shareEmote(String emoteName) {
		Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("image/*");
	    
	    if(prefs.altSharingMethod())
	    	share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FileIO.getEmoteByName(emoteName)));
	    else
	    	share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileIO.copyTempEmote(emoteName))));
	    
	    startActivity(Intent.createChooser(share,"Share emote via"));
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void copyEmoteToClipboard(String emoteName) {
		
		int sdk = android.os.Build.VERSION.SDK_INT;
		
		emoteName = "[](/" + emoteName + ") ";
		
		if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
		    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getView().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		    clipboard.setText(emoteName);
		} else {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getView().getContext().getSystemService(Context.CLIPBOARD_SERVICE); 
		    android.content.ClipData clip = android.content.ClipData.newPlainText("ponymotes",emoteName);
		    clipboard.setPrimaryClip(clip);
		}
	}
	
	@SuppressLint("NewApi")
	public void search(String searchTerm) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
        	try {
            	new SearchASyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchTerm);
            } catch(Exception e) {

            }
        } else{
        	new SearchASyncTask().execute(searchTerm);
        }
	}
	
	private class SearchASyncTask extends AsyncTask<String, Void, ArrayList<String>> {
		
	    public SearchASyncTask() {
	    }
	    
		
	    @Override
	    protected ArrayList<String> doInBackground(String... params) {
			
	    	dbController.open();
	    	ArrayList<String> result = dbController.searchFor(params[0], prefs.searchLimit());
	    	dbController.close();

	        return result;
	    }
	    
	    protected void onPostExecute(ArrayList<String> result) {
	    	gridView.setNumColumns(prefs.numOfColumns());
	    	searchResults.setText(String.valueOf(result.size() + " " + getView().getContext().getString(R.string.numOfResults)));
	    	emoteAdapter.setItems(result);
	    	
	    	gridView.setSelection(0);
	    	
	    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
	    }
        
    }
	
	
}
