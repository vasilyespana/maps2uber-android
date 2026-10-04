package dev.vasilyespana.maps2uber.core.geo;

/**
 * Extracts the first http(s) URL from shared text (share-sheet payloads).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\tR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Ldev/vasilyespana/maps2uber/core/geo/UrlExtractor;", "", "()V", "TRAILING_PUNCT", "", "", "URL_RE", "Lkotlin/text/Regex;", "firstUrl", "", "text", "app_release"})
public final class UrlExtractor {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.text.Regex URL_RE = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.Character> TRAILING_PUNCT = null;
    @org.jetbrains.annotations.NotNull()
    public static final dev.vasilyespana.maps2uber.core.geo.UrlExtractor INSTANCE = null;
    
    private UrlExtractor() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String firstUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
        return null;
    }
}