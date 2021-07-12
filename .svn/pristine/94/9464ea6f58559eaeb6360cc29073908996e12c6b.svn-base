package com.kkl.kklplus.b2b.jdhome.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jd.open.api.sdk.response.AbstractResponse;

import java.io.Serializable;

/**
 * @Auther wj
 * @Date 2021/2/4 18:20
 */
public class AdsDspUploadPicResponse extends AbstractResponse {
    private ResultInfo returnType;

    public AdsDspUploadPicResponse() {
    }

    @JsonProperty("returnType")
    public void setReturnType(ResultInfo returnType) {
        this.returnType = returnType;
    }

    @JsonProperty("returnType")
    public ResultInfo getReturnType() {
        return this.returnType;
    }

    public class ResultInfo implements Serializable {
        private boolean success;
        private String data;
        private String errorMessage;
        private String errorSolution;

        @JsonProperty("errorMessage")
        public String getErrorMessage() {
            return errorMessage;
        }
        @JsonProperty("errorMessage")
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        @JsonProperty("errorSolution")
        public String getErrorSolution() {
            return errorSolution;
        }
        @JsonProperty("errorSolution")
        public void setErrorSolution(String errorSolution) {
            this.errorSolution = errorSolution;
        }
        public ResultInfo() {
        }

        @JsonProperty("success")
        public void setSuccess(boolean success) {
            this.success = success;
        }

        @JsonProperty("success")
        public boolean getSuccess() {
            return this.success;
        }

        @JsonProperty("data")
        public void setData(String data) {
            this.data = data;
        }

        @JsonProperty("data")
        public String getData() {
            return this.data;
        }


    }
}
