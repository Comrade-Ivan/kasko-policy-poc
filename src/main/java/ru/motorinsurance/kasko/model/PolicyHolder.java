package ru.motorinsurance.kasko.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolder {
    private String type; // "Физ.Лицо" или "Юр.Лицо"
    private String name;
    private Contact contact;
    private List<Document> documents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        private String phone;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String type;
        private String number;
        private String s3Path;
    }
}