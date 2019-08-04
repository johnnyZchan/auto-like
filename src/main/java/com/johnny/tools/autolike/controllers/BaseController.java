package com.johnny.tools.autolike.controllers;

import com.johnny.tools.autolike.beans.ResponseModel;
import com.johnny.tools.autolike.constants.CodeInfo;
import com.johnny.tools.autolike.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

public abstract class BaseController implements Serializable {

    public ResponseModel getOkResponseModel() {
        return getOkResponseModel(null);
    }

    public ResponseModel getOkResponseModel(Object data) {
        ResponseModel model = new ResponseModel();
        model.setCode(CodeInfo.CODE_OK);
        model.setMessage(MessageUtil.getMessage(CodeInfo.CODE_OK));
        model.setData(data);
        return model;
    }

    public ResponseModel getPartialOkResponseModel(List<String> failList) {
        ResponseModel model = new ResponseModel();
        model.setCode(CodeInfo.CODE_PARTIAL_OK);
        model.setMessage(MessageUtil.getMessage(CodeInfo.CODE_PARTIAL_OK, StringUtils.join(failList, ",")));
        model.setData(failList);
        return model;
    }
}
