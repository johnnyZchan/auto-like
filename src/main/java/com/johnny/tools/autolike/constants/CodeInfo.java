package com.johnny.tools.autolike.constants;

public class CodeInfo {

    /**
     * 系统级别的Code
     */
    public static final int CODE_OK = 0;
    public static final int CODE_PARTIAL_OK = 1;
    public static final int CODE_SYS_ERROR = 100001;

    /**
     * 业务级别的Code
     */
    public static final int CODE_SERVICE_ERROR = 400000;
    // 必须项不能为空
    public static final int CODE_PARAMS_NOT_NULL = 400001;
}
