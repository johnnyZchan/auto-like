package com.johnny.tools.autolike.handlers;

import com.johnny.tools.autolike.beans.ResponseModel;
import com.johnny.tools.autolike.constants.CodeInfo;
import com.johnny.tools.autolike.exceptions.ServiceException;
import com.johnny.tools.autolike.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class SpringExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseModel> handlerOtherExceptions(final Exception ex) {
        log.error(ex.getMessage(), ex);

        int code = CodeInfo.CODE_SYS_ERROR;
        String message = MessageUtil.getMessage(code);

        if (ex instanceof ServiceException) {
            ServiceException serviceException = (ServiceException)ex;
            code = serviceException.getCode()!=null?serviceException.getCode():CodeInfo.CODE_SERVICE_ERROR;
            message = StringUtils.isNotBlank(ex.getMessage())?ex.getMessage():MessageUtil.getMessage(CodeInfo.CODE_SERVICE_ERROR);
        }

        ResponseModel model = new ResponseModel();
        model.setCode(code);
        model.setMessage(message);
        return new ResponseEntity(model, HttpStatus.OK);
    }
}
