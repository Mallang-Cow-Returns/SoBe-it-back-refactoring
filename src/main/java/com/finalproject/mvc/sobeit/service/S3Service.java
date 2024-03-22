package com.finalproject.mvc.sobeit.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;
    private List<String> s3ImgUrlsNeedDelete;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드 (article버전)
    public String articleImageUpload(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return articleImageUpload(uploadFile);
    }

    private String articleImageUpload(File uploadFile) {
        String fileName = "article" + "/" + UUID.randomUUID();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    public String profileImageUpload(MultipartFile multipartFile, Long userSeq) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return profileImageUpload(uploadFile, userSeq);
    }

    private String profileImageUpload(File uploadFile, Long userSeq) {
        String fileName = "profile" + "/" + userSeq;
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(new ObjectMetadata()) // PublicRead 권한으로 업로드 됨
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        File convertFile = new File(uniqueFilename);
        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        }
        return Optional.of(convertFile);
    }

    public void deleteImage(String imageUrl){
        try {
            amazonS3Client.deleteObject(bucket, imageUrl);
        } catch (Exception e) {
            log.info("S3 파일이 삭제되지 못했습니다.");
            s3ImgUrlsNeedDelete.add(imageUrl);
        }
    }

    /**
     * 하루에 한번 씩 삭제가 필요한 목록들을 S3 저장소에서 삭제
     */
    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60 * 24)
    public void deleteNotUsingS3Img() {
        log.info("s3ImgUrlsNeedDelete = {}", s3ImgUrlsNeedDelete);
        s3ImgUrlsNeedDelete.forEach(key -> amazonS3Client.deleteObject(bucket, key));
        s3ImgUrlsNeedDelete.clear();
    }

}