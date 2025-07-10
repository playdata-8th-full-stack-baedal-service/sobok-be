package com.sobok.adminservice.admin.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size; // 요청한 페이지 크기(pageSize)
    private int totalPages; // 전체 페이지 수
    private long totalElements; // 전체 항목 수
    private boolean first; // 첫 페이지
    private boolean last; // 마지막 페이지
}