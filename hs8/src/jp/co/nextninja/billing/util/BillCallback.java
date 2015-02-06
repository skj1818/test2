package jp.co.nextninja.billing.util;

public interface BillCallback {
	public void doSuccessEnd();
	public void doErrorEnd(int err);
}