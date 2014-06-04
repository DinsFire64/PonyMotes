package com.dinsfire.ponymotes;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmoteAdapter extends BaseAdapter
{
    public ArrayList<String> items = new ArrayList<String>();
    private LayoutInflater inflater;

    public EmoteAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }
    
    public void setItems(ArrayList<String> items) {
    	this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

	@SuppressLint("NewApi")
	@Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ViewHolder holder;

        if(view == null)
        {
        	view = inflater.inflate(R.layout.emote_view, viewGroup, false);
        	
        	holder = new ViewHolder();
        	holder.emotePicture = (ImageView) view.findViewById(R.id.picture);
        	holder.name = (TextView) view.findViewById(R.id.text);
        	
			//view.setTag(R.id.picture, view.findViewById(R.id.picture));
			//view.setTag(R.id.text, view.findViewById(R.id.text));
        	
        	view.setTag(holder);
        }else {
        	holder = (ViewHolder) view.getTag();
        	holder.emotePicture.setImageResource(android.R.color.transparent);
        	holder.name.setText(items.get(i));
        }
        
        holder.position = i;
        
        holder.emotePicture.setImageResource(0);
        
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
        	try {
            	new ThumbnailTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i, holder, items.get(i));
            } catch(Exception e) {

            }
        } else{
        	new ThumbnailTask().execute(i, holder, items.get(i));
        }

        return view;
        
    }
    
    private static class ThumbnailTask extends AsyncTask<Object, Object, Drawable> {
        private int mPosition;
        private ViewHolder mHolder;
        private String mEmoteName;
        
		@Override
		protected Drawable doInBackground(Object... params) {
			
			mPosition = (int) params[0];
            mHolder = (ViewHolder) params[1];
            mEmoteName = (String) params[2];
            Drawable d = null;
            
            if(FileIO.emoteExistsInStorage(mEmoteName))
            	d = Drawable.createFromPath(FileIO.getEmotePath(mEmoteName));
            
			return d;
		}
		
		@Override
		protected void onPostExecute(Drawable d) {
	        if (mHolder.position == mPosition) {

	        	mHolder.emotePicture.setPadding(5, 5, 5, 5);

	            if(d != null) {
	            	mHolder.emotePicture.setImageDrawable(d);
	            	mHolder.name.setText("");
	            }
	            else {
	            	mHolder.emotePicture.setImageResource(R.drawable.question_mark);
	            	mHolder.name.setText(mEmoteName);
	            }
	        }
	    }
    }
    
    private static class ViewHolder {
        public ImageView emotePicture;
        public TextView name;
        int position;
    }
}