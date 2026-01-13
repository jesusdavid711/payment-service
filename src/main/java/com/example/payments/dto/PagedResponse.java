package com.example.payments.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Generic wrapper for paginated responses.
 */
@Schema(description = "Paginated response wrapper")
public class PagedResponse<T> {

    @Schema(description = "List of items in the current page")
    private List<T> content;

    @Schema(description = "Current page number (0-indexed)", examples = { "0" })
    private int page;

    @Schema(description = "Page size", examples = { "10" })
    private int size;

    @Schema(description = "Total number of elements", examples = { "100" })
    private long totalElements;

    @Schema(description = "Total number of pages", examples = { "10" })
    private int totalPages;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    // Getters and Setters

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
