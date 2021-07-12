package com.kkl.kklplus.b2b.jdhome.service;

import com.kkl.kklplus.b2b.jdhome.entity.PictureUpload;
import com.kkl.kklplus.b2b.jdhome.mapper.PicUploadMapper;
import com.kkl.kklplus.b2b.jdhome.utils.QuarterUtils;
import com.kkl.kklplus.entity.b2b.common.B2BProcessFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Auther wj
 * @Date 2021/2/5 15:21
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PicUploadService {
    @Autowired
    private PicUploadMapper picUploadMapper;

    public Integer insert(PictureUpload pictureUpload){
        pictureUpload.setUpdateById(pictureUpload.getCreateById());
        pictureUpload.preInsert();
        pictureUpload.setQuarter(QuarterUtils.getQuarter(pictureUpload.getCreateDt()));
        return picUploadMapper.insert(pictureUpload);
    }

    public void updateProcessFlag(PictureUpload pictureUpload){
        pictureUpload.preUpdate();
        picUploadMapper.updateProcessFlag(pictureUpload);
    }
}
