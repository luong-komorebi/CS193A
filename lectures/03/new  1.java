1. Define a layout with your own sketch!!!
2. 

public class MyItem
{
public string sName;
public string sTelNumber;
public ...... bmpPhoto;
}

public class MyAdapter extends ArrayAdapter<MyItem> {
private int layoutResourceId;
private List<MyItem> data;

public  MyAdapter(Context context, int layoutId, List<MyItem> list) {
 super(context, layoutResourceId, data);
 layoutResourceId = layoutId;
 data = list;
}

@Override
public View getView(int index, View row, ViewGroup parent) {
 row = getLayoutInflater().inflate(layoutResourceId, parent, false);

 TextView textFullname = (TextView) row.findViewById(R.id.textFullname);
 textFullname.setText(data.get(index).sName);


 return row;
 }





}