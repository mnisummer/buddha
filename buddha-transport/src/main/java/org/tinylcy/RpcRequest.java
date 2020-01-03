package org.tinylcy;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by chenyangli.
 */
public class RpcRequest implements BasicMessage {

    private static final AtomicLong REQUEST_ID = new AtomicLong(1L);

	/** 请求id */
	private Long requestId;
	/** 类名 */
    private String className;
	/** 方法名 */
    private String methodName;
	/** 参数类型 */
    private Class<?>[] paramTypes;
	/** 参数 */
    private Object[] params;

    public RpcRequest() {
        this.requestId = REQUEST_ID.getAndIncrement();
    }

    public RpcRequest(String className, String methodName,
                      Class<?>[] paramTypes, Object[] params) {
        this.requestId = REQUEST_ID.getAndIncrement();
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}