package com.kkl.kklplus.b2b.jdhome.mapper;

import com.kkl.kklplus.b2b.jdhome.entity.PictureUpload;
import com.kkl.kklplus.entity.b2bcenter.rpt.B2BOrderProcesslog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Auther wj
 * @Date 2021/2/4 16:43
 */
@Mapper
public interface PicUploadMapper {

    Integer insert(PictureUpload pictureUpload);

    void updateProcessFlag(PictureUpload pictureUpload);

    void batchInsert(List<PictureUpload> pictureUploads);
}
