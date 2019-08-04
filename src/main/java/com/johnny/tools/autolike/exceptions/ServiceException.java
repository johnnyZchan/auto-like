package com.johnny.tools.autolike.exceptions;

import com.johnny.tools.autolike.constants.CodeInfo;
import com.johnny.tools.autolike.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;

public class ServiceException extends BaseException{

    public ServiceException() {
        super(CodeInfo.CODE_SERVICE_ERROR, MessageUtil.getMessage(CodeInfo.CODE_SERVICE_ERROR));
    }

    public ServiceException(Integer code) {
        super(code, StringUtils.isNotBlank(MessageUtil.getMessage(code))?MessageUtil.getMessage(code):MessageUtil.getMessage(CodeInfo.CODE_SERVICE_ERROR));
    }

    public ServiceException(String message) {
        super(CodeInfo.CODE_SERVICE_ERROR, StringUtils.isNotBlank(MessageUtil.getMessage(message))?MessageUtil.getMessage(message):message);
    }

    public ServiceException(Integer code, String message) {
        super(code, message);
    }
}
