package com.arish.shoppersclub.dto.response;

import java.util.List;

/**
 * ============================================================================
 * PagedResponse<T>
 * ============================================================================
 *
 * Purpose:
 * A generic response wrapper for paginated endpoints.
 *
 * Why use a Page Wrapper instead of raw List?
 * - Returning a raw List<T> only gives the client items for the current page.
 * - The frontend client (UI) needs metadata such as total pages, total elements,
 *   current page number, and whether there are more pages available to render
 *   pagination controls (e.g. "Next", "Previous", "Page 1 of 5").
 *
 * Concept Explained:
 * - content: The actual items for the requested page slice.
 * - pageNumber: Current zero-based page index (0 = first page).
 * - pageSize: Number of items requested per page.
 * - totalElements: Total count of items existing in the database matching query.
 * - totalPages: Total number of pages calculated as (totalElements / pageSize).
 * - isLast: Boolean flag indicating if this is the final page.
 * ============================================================================
 */
public record PagedResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isLast
) {

}
