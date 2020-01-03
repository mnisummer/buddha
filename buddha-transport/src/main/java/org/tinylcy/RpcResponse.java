package org.tinylcy;

/**
 * Created by chenyangli.
 */
public class RpcResponse implements BasicMessage {
	/** 表明该响应是对应哪个请求的 */
    private Long requestId;
    private Throwable error;
    /** 响应结果 */
    private Object result;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", error=" + error +
                ", result=" + result +
                '}';
    }
}
