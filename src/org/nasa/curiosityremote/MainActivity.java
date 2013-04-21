package org.nasa.curiosityremote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import orders.ActionOrder;
import orders.ActionOrder.Type;
import orders.MoveOrder;
import orders.Order;
import orders.RotateOrder;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Client;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DragSortListener;

public class MainActivity extends Activity
{
	List<Order> orders = new ArrayList<Order>();
	private OrderSequenceAdapter adapter;
	private DragSortListView listView;
	private Client client;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		setContentView(R.layout.activity_main);
		listView = (DragSortListView) findViewById(R.id.listView);
//		listView = new DragSortListView(this, null);
//		setContentView(listView);
		
		adapter = new OrderSequenceAdapter();
		listView.setAdapter(adapter);
		listView.setDragListener(adapter);
		
		client = new Client();
		client.start();
		try
		{
			client.connect(5000, "192.168.31.190", 8001, 8002);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		client.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_item_move:
			orders.add(new MoveOrder(1));
			updateList();
			return true;
		case R.id.menu_item_rotate:
			orders.add(new RotateOrder(0));
			updateList();
			return true;
		case R.id.menu_item_laser:
			orders.add(new ActionOrder(ActionOrder.Type.LASER));
			updateList();
			return true;
		case R.id.menu_item_dig:
			orders.add(new ActionOrder(ActionOrder.Type.DIG));
			updateList();
			return true;
		case R.id.menu_item_cam:
			orders.add(new ActionOrder(ActionOrder.Type.CAM));
			updateList();
			return true;
		case R.id.menu_item_go:
			return true;
		default:
			return false;
		}
	}
	
	private void updateList()
	{
		adapter.notifyDataSetChanged();
		listView.post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	            listView.setSelection(adapter.getCount() - 1);
	        }
	    });
		scheduleSendProgram();
	}
	
	private void scheduleSendProgram()
	{
		client.sendTCP(TextUtils.join(";", orders));
	}
	
	class OrderSequenceAdapter extends BaseAdapter implements DragSortListener
	{
		
		public OrderSequenceAdapter()
		{
		}

		@Override
		public int getCount()
		{
			return orders != null ? orders.size() : 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return null;
		}

		@Override
		public long getItemId(int arg0)
		{
			return 0;
		}
		
//		@Override
//		public int getViewTypeCount()
//		{
//			return 2;
//		}
		
		@Override
		public View getView(final int pos, View convertView, ViewGroup arg2)
		{
			if (convertView == null)
			{
				convertView = View.inflate(arg2.getContext(), R.layout.order_item, null);
			}
			ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
			SeekBar seekBar = (SeekBar) convertView.findViewById(R.id.seekBar1);
			seekBar.setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.buttonDelete).setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					remove(pos);
				}
			});
			final TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			Order order = orders.get(pos);
			
			if (order instanceof ActionOrder)
			{
				int res = 0;
				String text = "";
				switch (((ActionOrder) order).getType()) {
				case LASER:
					res = R.drawable.icon_laser;
					text = "Tir ChemCam";
					break;
				case DIG:
					res = R.drawable.camera;
					text = "Forage roche";
					break;
				case CAM:
					res = R.drawable.camera;
					text = "Caméra panoramique";
					break;
				default:
					break;
				}
				tv.setText(text);
				imageView.setImageResource(res);
				seekBar.setVisibility(View.INVISIBLE);
			}
			else if (order instanceof MoveOrder)
			{
				final MoveOrder moveOrder = (MoveOrder) order;
				float distance = moveOrder.distance;
				seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser)
					{
						//if (fromUser)
						{
							float distance = progress / 10;
							moveOrder.distance = distance;
							tv.setText("Distance " + distance + " m");
							scheduleSendProgram();
						}
					}
				}); 
				seekBar.setMax(100);
				seekBar.setProgress((int) (distance * 10));
				tv.setText("Distance " + distance + " m");
				imageView.setImageResource(R.drawable.up_arrow);
			}
			
			else if (order instanceof RotateOrder)
			{
				final RotateOrder rotOrder = (RotateOrder) order;
				float angle = rotOrder.angle;
				seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser)
					{
						//if (fromUser)
						{
							float angle = progress - 90;
							rotOrder.angle = angle;
							tv.setText("Angle " + angle + " °");
							scheduleSendProgram();
						}
					}
				}); 
				seekBar.setMax(180);
				seekBar.setProgress((int) (angle + 90));
				tv.setText("Angle " + angle + " °");
				imageView.setImageResource(R.drawable.rotate);
			}
			
			return convertView;
		}

		@Override
		public void drag(int from, int to)
		{
			
		}

		@Override
		public void drop(int from, int to)
		{
			orders.add(to, orders.remove(from));
			notifyDataSetChanged();
			scheduleSendProgram();
		}

		@Override
		public void remove(int which)
		{
			orders.remove(which);
			notifyDataSetChanged();
			scheduleSendProgram();
		}
		
		
	}

}
