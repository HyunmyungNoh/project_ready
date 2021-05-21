package edu.axboot.fileupload;

import com.chequer.axboot.core.api.ApiException;
import com.chequer.axboot.core.code.ApiStatus;
import com.chequer.axboot.core.code.Types;
import com.chequer.axboot.core.controllers.BaseController;
import edu.axboot.domain.file.UploadParameters;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService implements InitializingBean {

    @Value("${axboot.upload.repository}")
    public String uploadRepository;

    @Value("${axboot.upload.maxFolderFiles}")
    public int uploadFolderMaxFile;

    @Value("${axboot.download.zip}")
    public String downloadZip;

    @Value("${axboot.upload.maxFileSize}")
    public int uploadSize;

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    public void createBaseDirectory() {
        try {
            FileUtils.forceMkdir(new File(uploadRepository));
        } catch (IOException e) {
        }
    }

    public String getTempDir() {
        return System.getProperty("java.io.tmpdir");
        //uploadRepository ---> /home/upload/
        //return uploadRepository + "/tmp";
    }

    public File multiPartFileToFile(MultipartFile multipartFile) throws IOException {
        String baseDir = getTempDir() + "/" + UUID.randomUUID().toString().replaceAll("..","");
        baseDir = baseDir.replaceAll("&","");

        FileUtils.forceMkdir(new File(baseDir));

        String tmpFileName = baseDir + "/" + FilenameUtils.getName(multipartFile.getOriginalFilename());
        File file = new File(tmpFileName);

        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(multipartFile.getBytes());
        stream.close();

        return file;
    }

    private String getFileType(String extension) {
        switch (extension.toUpperCase()) {
            case Types.FileExtensions.PNG:
            case Types.FileExtensions.JPG:
            case Types.FileExtensions.JPEG:
            case Types.FileExtensions.GIF:
            case Types.FileExtensions.BMP:
            case Types.FileExtensions.TIFF:
            case Types.FileExtensions.TIF:
                return Types.FileType.IMAGE;

            case Types.FileExtensions.PDF:
                return Types.FileType.PDF;

            default:
                return Types.FileType.ETC;
        }
    }

    public String getBasePath() {
        return uploadRepository;
    }

    public String getSavePath(String saveName) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy");
        String yy = dt.format(new Date());

        File dir = new File(getBasePath() + "/" + yy);

        if(!dir.exists()){
            dir = new File(getBasePath() + "/" + yy + "/0001/thumb");
            dir.mkdirs();

            dir = new File(getBasePath() + "/" + yy + "/0001");
        }
        else{
            File[] dirList = new File(getBasePath() + "/" + yy).listFiles(File::isDirectory);
            File[] fileList = new File(getBasePath() + "/" + yy + "/" + dirList[dirList.length-1].getName()).listFiles(File::isFile);

            if (fileList != null && fileList.length >= uploadFolderMaxFile){
                String newFolder = "000" + (dirList.length+1);
                newFolder = newFolder.substring(newFolder.length()-4, newFolder.length());

                dir = new File(getBasePath() + "/" + yy + "/" + newFolder + "/thumb");
                dir.mkdirs();

                dir = new File(getBasePath() + "/" + yy + "/" + newFolder);
            }
            else{
                dir = dirList[dirList.length-1];
            }
        }
        return dir + "/" + saveName;

    }

    public byte[] getFileBytes(String saveName) throws IOException {
        return FileUtils.readFileToByteArray(new File(getSavePath(saveName)));
    }

    public UploadFile addCommonFile(UploadParameters uploadParameters) throws Exception {
        File uploadFile = uploadParameters.getFile();

        if (uploadFile == null && uploadParameters.getMultipartFile() != null) {
            uploadFile = multiPartFileToFile(uploadParameters.getMultipartFile());
        }

        if (uploadFile.length() > uploadSize) {
            try {
                throw new ApiException(ApiStatus.SYSTEM_ERROR, "업로드 파일크기는 " + FileUtils.byteCountToDisplaySize(uploadSize) + " 이하로 가능합니다.");
            }
            catch (Error e) {
                logger.info(e.toString());
            }
        }

        String targetId = uploadParameters.getTargetId();
        String desc = uploadParameters.getDesc();

        boolean deleteIfExist = uploadParameters.isDeleteIfExist();
        boolean thumbnail = uploadParameters.isThumbnail();

        int sort = uploadParameters.getSort();
        int thumbnailWidth = uploadParameters.getThumbnailWidth();
        int thumbnailHeight = uploadParameters.getThumbnailHeight();

        String fileName = FilenameUtils.getName(uploadFile.getName());
        String extension = FilenameUtils.getExtension(fileName);
        String fileType = extension.toLowerCase();
        String mimeType= URLConnection.guessContentTypeFromName(uploadFile.getName());

        List<String> allowFile = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp"
                , "mp4", "wmv", "mp3", "mov", "avi", "mpeg", "flv"
                , "xls", "xlsx", "doc", "docx", "ppt", "pptx", "hwp", "pdf", "txt", "zip"
        );

        if (allowFile.stream().filter(m -> m.equals(fileType)).count()==0) {
            try {
                throw new ApiException(ApiStatus.SYSTEM_ERROR, "업로드 가능한 파일은 문서, 이미지, 압축파일만 가능합니다.");
            }
            catch (Error e) {
                logger.info(e.toString());
            }
        }

        String baseName = UUID.randomUUID().toString().replaceAll("-","");
        String saveName = baseName + "." + extension;
        String savePath = getSavePath(saveName);

        File file = new File(savePath);

        FileUtils.copyFile(uploadFile, file);

        if (fileType.equals(Types.FileType.IMAGE) && thumbnail) {
            try {
                Thumbnails.of(file)
                        .crop(Positions.CENTER)
                        .size(thumbnailWidth, thumbnailHeight)
                        .toFiles(new File(getBasePath()), Rename.SUFFIX_HYPHEN_THUMBNAIL);
            } catch (Exception e) {
                logger.info(e.toString());
            }
        }

        FileUtils.deleteQuietly(uploadFile);

        UploadFile commonFile = UploadFile.UploadFileBuilder
                .aUploadFile()
                .withFileNm(fileName)
                .withSaveNm(saveName)
                .withSavePath(savePath.replace(uploadRepository, ""))
                .withMineType(mimeType)
                .withExtension(extension)
                .withFileSize(file.length())
                .build();

        return commonFile;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createBaseDirectory();
    }

    public void deleteFile(String savePath) {
        File file = new File(savePath);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }
    }
}
