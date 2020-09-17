package com.daoyis.shennghuang.utils;

import android.content.Context;
import android.content.Intent;

public class ScannerInterface {
	
	/********************************************ɨ��ӿڳ�������******************************/
	//����ر�ɨ��ͷ
	//4.2.1����ǰ
	// public static final String KEY_BARCODE_ENABLESCANNER_ACTION = "android.intent.action.BARCODESCAN";
	//4.3.1���Ժ�
	public static final String KEY_BARCODE_ENABLE_ACTION = "android.intent.action.BARCODESCAN";

	//��ʼɨ��
	public static final String KEY_BARCODE_STARTSCAN_ACTION = "android.intent.action.BARCODESTARTSCAN";
	//ֹͣɨ��
	public static final String KEY_BARCODE_STOPSCAN_ACTION = "android.intent.action.BARCODESTOPSCAN";

	//����ɨ�谴��
	public static final String KEY_LOCK_SCAN_ACTION = "android.intent.action.BARCODELOCKSCANKEY";
	//�ͷ�ɨ�谴��
	public static final String KEY_UNLOCK_SCAN_ACTION = "android.intent.action.BARCODEUNLOCKSCANKEY";
	//ɨ��ɹ���ʾ��
	public static final String KEY_BEEP_ACTION = "android.intent.action.BEEP";
	//ɨ��ʧ����ʾ��
	public static final String KEY_FAILUREBEEP_ACTION = "android.intent.action.FAILUREBEEP";
	//����ʾ
	public static final String KEY_VIBRATE_ACTION = "android.intent.action.VIBRATE";
	//�Ƿ�㲥ģʽ
	public static final String KEY_OUTPUT_ACTION = "android.intent.action.BARCODEOUTPUT";
	//�㲥���ñ����ʽ
	public static final String KEY_CHARSET_ACTION = "android.intent.actionCHARSET";
	//ʡ��ģʽ
	public static final String KEY_POWER_ACTION = "android.intent.action.POWER";
	//��������
	public static final String KEY_TERMINATOR_ACTION = "android.intent.TERMINATOR";
	//֪ͨ��ͼ����ʾ
	public static final String KEY_SHOWNOTICEICON_ACTION  = "android.intent.action.SHOWNOTICEICON";
	//APPͼ����ʾ
	public static final String KEY_SHOWICON_ACTION  = "android.intent.action.SHOWAPPICON";

	//��ɨ�����ý���
	public static final String KEY_SHOWISCANUI = "com.android.auto.iscan.show_setting_ui";

	//���ǰ׺
	public static final String KEY_PREFIX_ACTION = "android.intent.action.PREFIX";
	//��׺
	public static final String KEY_SUFFIX_ACTION = "android.intent.action.SUFFIX";
	//��ȡ���ַ�
	public static final String KEY_TRIMLEFT_ACTION = "android.intent.action.TRIMLEFT";
	//��ȡ���ַ�
	public static final String KEY_TRIMRIGHT_ACTION = "android.intent.action.TRIMRIGHT";
	//���ϲ�Led�ƹ����
	public static final String KEY_LIGHT_ACTION = "android.intent.action.LIGHT";
	//���ó�ʱʱ��
	public static final String KEY_TIMEOUT_ACTION = "android.intent.action.TIMEOUT";
	//�����ض��ַ�
	public static final String KEY_FILTERCHARACTER_ACTION = "android.intent.action.FILTERCHARACTER";

	//��ɨ 4.2.1��֮ǰ�汾
	//public static final String KEY_CONTINUCESCAN_ACTION = "android.intent.action.BARCODECONTINUCESCAN";
	//��ɨ 4.3.1��֮��汾
	public static final String KEY_CONTINUCESCAN_ACTION = "android.intent.action.CONTINUCESCAN";

	//����ɨ����ʱ��
	public static final String KEY_INTERVALTIME_ACTION = "android.intent.action.INTERVALTIME";
	//�Ƿ�ɾ���༭������
	public static final String KEY_DELELCTED_ACTION = "android.intent.action.DELELCTED";
	//�ָ�Ĭ������
	public static final String KEY_RESET_ACTION = "android.intent.action.RESET";
	//ɨ�谴������
	public static final String SCANKEY_CONFIG_ACTION = "android.intent.action.scankeyConfig";

	//ɨ��ʧ�ܹ㲥
	public static final String KEY_FAILUREBROADCAST_ACTION = "android.intent.action.FAILUREBROADCAST";

	//���ý�������
	public static final String KEY_SETMAXMULTIREADCOUNT_ACTION = "android.intent.action.MAXMULTIREADCOUNT";
	/****************************************************************************************************/

	/********************************************ϵͳ�ӿڶ��峣��*****************************/
	static final String SET_STATUSBAR_EXPAND = "com.android.set.statusbar_expand";
	static final String SET_USB_DEBUG = "com.android.set.usb_debug";
	static final String SET_INSTALL_PACKAGE = "com.android.set.install.package";
	static final String SET_SCREEN_LOCK = "com.android.set.screen_lock";
	static final String SET_CFG_WAKEUP_ANYKEY = "com.android.set.cfg.wakeup.anykey";
	static final String SET_UNINSTALL_PACKAGE= "com.android.set.uninstall.package";
	static final String SET_SYSTEM_TIME="com.android.set.system.time";
	static final String SET_KEYBOARD_CHANGE = "com.android.disable.keyboard.change";
	static final String SET_INSTALL_PACKAGE_WITH_SILENCE = "com.android.set.install.packege.with.silence";
	static final String SET_INSTALL_PACKAGE_EXTRA_APK_PATH = "com.android.set.install.packege.extra.apk.path";
	static final String SET_INSTALL_PACKAGE_EXTRA_TIPS_FORMAT = "com.android.set.install.packege.extra.tips.format";
	static final String SET_SIMULATION_KEYBOARD = "com.android.simulation.keyboard";
	static final String SET_SIMULATION_KEYBOARD_STRING = "com.android.simulation.keyboard.string";
	/****************************************************************************************************/

	private Context mContext;
	private static ScannerInterface androidjni;

	public ScannerInterface(Context context) {
		mContext = context;
	}

	/*********ɨ�� ���ƽӿ�*********************/

	//	1.��ɨ�����ý���
	public void ShowUI(){	
		if(mContext != null){
			Intent intent = new Intent(KEY_SHOWISCANUI);
			mContext.sendBroadcast(intent);
		}
	}

	//	2.��ɨ��ͷ��Դ
	/**Ƶ������open() �ر�close() ������reset() ɨ�������Դ�ӿ����׵��´��ڿ������뾡���ٵ��ã�
	 * һ���ڳ����ʱ����һ��open()��reset����j�ӿڣ��˳�ʱ����close�����ӿڣ����߾�����Ҫ����
	 * ����iScan�Զ�����*/
	//4.2.1��֮ǰ�㲥action  KEY_BARCODE_ENABLESCANNER_ACTION
	//4.3.1��֮��㲥action  KEY_BARCODE_ENABLE_ACTION
	public void open(){	
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_ENABLE_ACTION);
			intent.putExtra(KEY_BARCODE_ENABLE_ACTION, true);
			mContext.sendBroadcast(intent);
		}
	}

	//2���ر�ɨ��ͷ��Դ
	/**Ƶ������open() �ر�close() ������reset() ɨ�������Դ�ӿ����׵��´��ڿ������뾡���ٵ��ã�
	 * һ���ڳ����ʱ����һ��open()��reset����j�ӿڣ��˳�ʱ����close�����ӿڣ����߾�����Ҫ����
	 * ����iScan�Զ�����*/
	public void  close(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_ENABLE_ACTION);
			intent.putExtra(KEY_BARCODE_ENABLE_ACTION, false);
			mContext.sendBroadcast(intent);
		}

	}

	// 3. ����ɨ��ͷ��ɨ��ͷ����
	/**
	 * �˺����� scan_stop ���ʹ�ÿ����ڳ������������ɨ��ͷ����ɨ��ͷ���ڿ���״
 ̬ʱ,���� scan_start ���Դ���ɨ��ͷ����ɨ�衣ɨ����ϻ�ʱ��,�������
scan_start �ָ�ɨ��ͷ״̬��
	 * 
	 * */
	public void  scan_start(){

		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_STARTSCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}

	//4.ֹͣɨ��ͷ���룬ɨ��ͷ���
	/**
	 * �˺����� scan_stop ���ʹ�ÿ����ڳ������������ɨ��ͷ����Ӧ�ó������
	 scan_start ����ɨ��ͷ����ɨ���, ������� scan_stop �ָ�ɨ��ͷ״̬��
	 */
	public void scan_stop(){
		if(mContext != null){
			Intent intent = new Intent(KEY_BARCODE_STOPSCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}

	/***�����豸��ɨ�谴����������ֻ��ͨ��iScan�����ɨ�谴������ɨ�裬�û��޷��Զ��尴����
	 */
	public void  lockScanKey(){
		if(mContext != null){
			Intent intent = new Intent(KEY_LOCK_SCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}

	/******
	 *�����ɨ�谴���������������iScan�޷�����ɨ������û����Զ��尴����
	 */
	public void unlockScanKey(){
		if(mContext != null){
			Intent intent = new Intent(KEY_UNLOCK_SCAN_ACTION);
			mContext.sendBroadcast(intent);
		}
	}


	/**ɨ��ͷ�����ģʽ
	 * mode 0:ɨ����ֱ�ӷ��͵�����༭����
	 * mode 1:ɨ�����Թ㲥ģʽ���ͣ�Ӧ�ó�����Ҫע��actionΪ��android.intent.action.SCANRESULT���Ĺ㲥���������ڹ㲥���� onReceive(Context context, Intent arg1) ������,ͨ���������
	 String  barocode=arg1.getStringExtra("value");
	 int barocodelen=arg1.getIntExtra("length",0);
	 �ֱ��� ����ֵ,���볤��,��������
	 mode 2:ģ�ⰴ�����ģʽ
	 */
	public void setOutputMode(int mode){
		if(mContext != null){
			Intent intent = new Intent(KEY_OUTPUT_ACTION);
			intent.putExtra(KEY_OUTPUT_ACTION, mode);
			mContext.sendBroadcast(intent);
		}
	}
	
	/**8 �Ƿ񲥷�����*/
	public void enablePlayBeep(boolean enable){
		if(mContext != null){
			Intent intent = new Intent(KEY_BEEP_ACTION);
			intent.putExtra(KEY_BEEP_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}
	
	/**ɨ��ʧ���Ƿ񲥷�����*/
	public void enableFailurePlayBeep(boolean enable){
		if(mContext != null){
			Intent intent = new Intent(KEY_FAILUREBEEP_ACTION);
			intent.putExtra(KEY_FAILUREBEEP_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}


	/**9 �Ƿ���*/
	public void enablePlayVibrate(boolean enable){
		if(mContext != null){
			Intent intent = new Intent(KEY_VIBRATE_ACTION);
			intent.putExtra(KEY_VIBRATE_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}

	/**  ���ӻس������е�
	 * 0 <item>��</item>
	 1 <item>���ӻس���</item>
	 2 <item>����TAB��</item>
	 3 <item>���ӻ��з�</item>*/
	public void  enableAddKeyValue(int value){
		if(mContext != null){
			Intent intent = new Intent(KEY_TERMINATOR_ACTION);
			intent.putExtra(KEY_TERMINATOR_ACTION, value);
			mContext.sendBroadcast(intent);
		}
	}
	
	/************************************************************/

	//���ǰ׺
	public void  addPrefix(String text){
		if(mContext != null){
			Intent intent = new Intent(KEY_PREFIX_ACTION);
			intent.putExtra(KEY_PREFIX_ACTION, text);
			mContext.sendBroadcast(intent);
		}
	}

	//��Ӻ�׺
	public void  addSuffix(String text){
		if(mContext != null){
			Intent intent = new Intent(KEY_SUFFIX_ACTION);
			intent.putExtra(KEY_SUFFIX_ACTION, text);
			mContext.sendBroadcast(intent);
		}
	}

	//��ȡ���ַ�
	public void   interceptTrimleft  (int  num){
		if(mContext != null){
			Intent intent = new Intent(KEY_TRIMLEFT_ACTION);
			intent.putExtra(KEY_TRIMLEFT_ACTION, num);
			mContext.sendBroadcast(intent);
		}
	}

	//��ȡ���ַ�
	public void   interceptTrimright  (int  num){
		if(mContext != null){
			Intent intent = new Intent(KEY_TRIMRIGHT_ACTION);
			intent.putExtra(KEY_TRIMRIGHT_ACTION, num);
			mContext.sendBroadcast(intent);
		}
	}

	//�Ҳ�Led�ƹ����
	public void   lightSet (boolean enable ){
		if(mContext != null){
			Intent intent = new Intent(KEY_LIGHT_ACTION);
			intent.putExtra(KEY_LIGHT_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}

	//���ó�ʱʱ��
	public void   timeOutSet(int  value){
		if(mContext != null){
			Intent intent = new Intent(KEY_TIMEOUT_ACTION);
			intent.putExtra(KEY_TIMEOUT_ACTION, value);
			mContext.sendBroadcast(intent);
		}
	}

	//�����ض��ַ�
	public void   filterCharacter (String text ){
		if(mContext != null){
			Intent intent = new Intent(KEY_FILTERCHARACTER_ACTION);
			intent.putExtra(KEY_FILTERCHARACTER_ACTION, text);
			mContext.sendBroadcast(intent);
		}
	}

	// �Ƿ���ɨ
	public void   continceScan (boolean enable ){
		if(mContext != null){
			Intent intent = new Intent(KEY_CONTINUCESCAN_ACTION);
			intent.putExtra(KEY_CONTINUCESCAN_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}

	//����ɨ����ʱ��
	public void  intervalSet(int  value){
		if(mContext != null){
			Intent intent = new Intent(KEY_INTERVALTIME_ACTION);
			intent.putExtra(KEY_INTERVALTIME_ACTION, value);
			mContext.sendBroadcast(intent);
		}
	}
	//ɨ��ʧ�ܹ㲥
	public void   SetErrorBroadCast (boolean enable ){
		if(mContext != null){
			Intent intent = new Intent(KEY_FAILUREBROADCAST_ACTION);
			intent.putExtra(KEY_FAILUREBROADCAST_ACTION, enable);
			mContext.sendBroadcast(intent);
		}
	}

	//�ָ�Ĭ������
	public void resultScan(){	
		if(mContext != null){
			Intent intent = new Intent(KEY_RESET_ACTION);
			mContext.sendBroadcast(intent);
		}
	}
}
