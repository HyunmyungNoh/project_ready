/* 일종의 엔티티 파일 */

package edu.axboot.fileupload;

import com.chequer.axboot.core.annotations.Comment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;

@Setter
@Getter
public class UploadFile {
    @Comment(value = "ID")
    private Long id;

    @Comment(value = "실제 파일명")
    private String fileNm;

    @Comment(value = "저장 파일명")
    private String saveNm;

    @Comment(value = "저장 경로")
    private String savePath;

    @Comment(value = "파일 타입")
    private String mineType;

    @Comment(value = "확장자")
    private String extension;

    @Comment(value = "파일 크기")
    private Long fileSize;

    @Comment(value = "AWS S3 경로")
    private String s3Url;

    @JsonIgnore
    @Transient
    private String _preview;

    @JsonIgnore
    @Transient
    private String _thumbnail;

    @JsonIgnore
    @Transient
    private String _download;

    public UploadFile(String savePath, String fileNm) {
        this.savePath = savePath;
        this.fileNm = fileNm;
    }

    public UploadFile() {

    }

    @JsonProperty("preview")
    public String preview() {
        if (StringUtils.isEmpty(_preview)) {
            return "/api/v1/files/preview?filePath=" + getSavePath();
        }
        return _preview;
    }

    @JsonProperty("thumbnail")
    public String thumbnail() {
        if (StringUtils.isEmpty(_thumbnail)) {
            return "/api/v1/files/thumbnail?id=" + getId();
        }
        return _thumbnail;
    }

    @JsonProperty("download")
    public String download() {
        if (StringUtils.isEmpty(_download)) {
            return "/api/v1/download?filePath=" + getSavePath() + "&fileNm=" + getFileNm();
        }
        return _download;
    }

    public static final class UploadFileBuilder {
        private Long id;
        private String fileNm;
        private String saveNm;
        private String savePath;
        private String mineType;
        private String extension;
        private Long fileSize;
        private String _preview;
        private String _thumbnail;
        private String _download;

        private UploadFileBuilder() {
        }

        public static UploadFileBuilder aUploadFile() {
            return new UploadFileBuilder();
        }

        public UploadFileBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UploadFileBuilder withFileNm(String fileNm) {
            this.fileNm = fileNm;
            return this;
        }

        public UploadFileBuilder withSaveNm(String saveNm) {
            this.saveNm = saveNm;
            return this;
        }

        public UploadFileBuilder withSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public UploadFileBuilder withMineType(String mineType) {
            this.mineType = mineType;
            return this;
        }

        public UploadFileBuilder withExtension(String extension) {
            this.extension = extension;
            return this;
        }

        public UploadFileBuilder withFileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public UploadFileBuilder with_preview(String _preview) {
            this._preview = _preview;
            return this;
        }

        public UploadFileBuilder with_thumbnail(String _thumbnail) {
            this._thumbnail = _thumbnail;
            return this;
        }

        public UploadFileBuilder with_download(String _download) {
            this._download = _download;
            return this;
        }

        public UploadFile build() {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setId(id);
            uploadFile.setFileNm(fileNm);
            uploadFile.setSaveNm(saveNm);
            uploadFile.setSavePath(savePath);
            uploadFile.setMineType(mineType);
            uploadFile.setExtension(extension);
            uploadFile.setFileSize(fileSize);
            uploadFile.set_preview(_preview);
            uploadFile.set_thumbnail(_thumbnail);
            uploadFile.set_download(_download);
            return uploadFile;
        }
    }
}
