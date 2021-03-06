package zz.itcast.redbody7.activity;

import java.util.List;

import zz.itcast.redbody7.R;
import zz.itcast.redbody7.MyConstant.MyConstants;
import zz.itcast.redbody7.adapter.DefaultBaseAdapter;
import zz.itcast.redbody7.bean.AddressBean;
import zz.itcast.redbody7.bean.AddressListBean;
import zz.itcast.redbody7.utils.LogPrint;
import zz.itcast.redbody7.view.TextContentView;
import zz.itcast.redbody7.view.TittleBarView;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 地址管理页面
 * 
 * @author ChengQin
 * 
 */
public class AddressManageActivity extends BaseActivity {

	private List<AddressBean> addressList;

	/**
	 * 标题栏 自定义控件
	 */
	private TittleBarView mTbv;

	private ShowAddressAdapter adapter;

	/**
	 * 有数据 Listview
	 */
	private ListView mListView;

	/**
	 * 自定义控件 textContentView
	 */
	private TextContentView contentView;

	/**
	 * 地址信息
	 */
	private TextContentView textContentView;

	@Override
	public void initRegister() {
		/**
		 * 返回点事件
		 */
		mTbv.setOnBackClick(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		/**
		 * 新增地址点击事件
		 */
		mTbv.setOnOperClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, NewAdrressActivity.class);

				startActivityForResult(intent, 66);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 66) {
			switch (resultCode) {
			case 110:
				if (data != null) {
					String name = data.getStringExtra("name");
					String phoneNumber = data.getStringExtra("phoneNumber");
					String addressDetail = data.getStringExtra("addressDetail");

					AddressBean address = new AddressBean();
					address.name = name;
					address.phoneNumber = phoneNumber;
					address.addressDetail = addressDetail;
					addressList.add(0, address);
				}
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		} else if (requestCode == 67) {
			switch (resultCode) {
			case 116:
				if (data != null) {

					initData();
					adapter.notifyDataSetChanged();
				}
				break;

			}
		}

	}

	class addressInfo {
		int id;
		String phoneNumber;
		String addressDetail;
		String name;

	}

	@Override
	/**
	 * 初始化数据
	 */
	public void initData() {

		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		httpUtils.send(HttpMethod.POST, MyConstants.BASE_URL + "/addresslist",
				new RequestCallBack<String>() {

					@Override
					/**
					 * 请求成功
					 * @param arg0
					 */
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.i("qc", "地址获取成功：" + result);
						parseJson(result);
					}

					@Override
					/**
					 * 请求失败
					 * @param arg0
					 * @param arg1
					 */
					public void onFailure(HttpException error, String msg) {
						error.printStackTrace();
						LogPrint.logI("AddressManageActivity", msg);
					}

				});
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_address);
		// 有数据的布局
		mListView = (ListView) findViewById(R.id.listview);

		// TODO 没数据的布局

		mTbv = (TittleBarView) findViewById(R.id.tbv);
		mTbv.setTittle("地址管理");
		mTbv.setOperText("新增地址");
		mTbv.setTextSize(15);

		iv_empty = (ImageView) findViewById(R.id.iv_empty);
		tv_empty = (TextView) findViewById(R.id.tv_empty_text);
	}

	/**
	 * 解析json数据
	 */
	private void parseJson(String result) {
		Gson gson = new Gson();
		AddressListBean addressListBean = gson.fromJson(result,
				AddressListBean.class);
		addressList = addressListBean.addresslist;
		adapter = new ShowAddressAdapter(addressList);
		mListView.setAdapter(adapter);

		/**
		 * 条目点击事件
		 */
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentPosition = position;
				// adapter.notifyDataSetChanged();
				// 跳转到修改地址页面
				Intent data = new Intent(mContext, UpdateAddressActivity.class);
				// 数据源
				AddressBean addressBean = addressList.get(position);
				// 点击条目传递给修改信息界面
				data.putExtra("id", addressBean.id);
				data.putExtra("name", addressBean.name);
				data.putExtra("phoneNumber", addressBean.phoneNumber);
				data.putExtra("addressDetail", addressBean.addressDetail);

				// 点击条目获取删除地址返回的结果
				startActivityForResult(data, 67);

			}
		});
	}

	private int currentPosition = 0;

	private ImageView iv_empty;

	private TextView tv_empty;

	public class ShowAddressAdapter extends DefaultBaseAdapter<AddressBean> {

		public ShowAddressAdapter(List<AddressBean> datas) {
			super(datas);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			textContentView = new TextContentView(mContext);
			if (!addressList.isEmpty()) { // 有数据
				mListView.setVisibility(View.VISIBLE);
				iv_empty.setVisibility(View.GONE);
				tv_empty.setVisibility(View.GONE);

				// 添加数据
				AddressBean addressBean = addressList.get(position);
				int id = addressBean.id; // id
				String addressDetail = addressBean.addressDetail; // 地址
				String name = addressBean.name;
				String phoneNumber = addressBean.phoneNumber;

				if (currentPosition == position) {
					textContentView.setImageResId(R.drawable.ok);
				} else {
					textContentView.setVisibility(false);
				}
				textContentView.setAddress("地址：" + addressDetail);
				textContentView.setName("姓名：" + name);
				textContentView.setNumber("电话：" + phoneNumber);

			} else { // 没有数据
				Toast.makeText(getApplicationContext(), "没有数据",
						Toast.LENGTH_SHORT).show();
				mListView.setVisibility(View.GONE);
				iv_empty.setVisibility(View.VISIBLE);
				tv_empty.setVisibility(View.VISIBLE);
			}

			return textContentView;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		initData();
		initRegister();
	}

}
