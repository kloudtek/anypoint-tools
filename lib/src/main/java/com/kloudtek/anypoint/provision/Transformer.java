package com.kloudtek.anypoint.provision;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Transformer {
    boolean appliesTo( String filename );
    byte[] transform(String entryPath, @Nullable byte[] data) throws Exception;
    @NotNull
    List<String> getNewFiles();
}
