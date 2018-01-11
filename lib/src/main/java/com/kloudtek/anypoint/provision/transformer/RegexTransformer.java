package com.kloudtek.anypoint.provision.transformer;

import com.kloudtek.anypoint.provision.Transformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RegexTransformer implements Transformer {
    private String fileRegex;
    private String regexMatch;
    private String replacementValue;
    private boolean replaceFirst;
    private String encoding = "UTF-8";

    public RegexTransformer() {
    }

    public RegexTransformer(String fileRegex, String regexMatch, String replacementValue, boolean replaceFirst) {
        this.fileRegex = fileRegex;
        this.regexMatch = regexMatch;
        this.replacementValue = replacementValue;
        this.replaceFirst = replaceFirst;
    }

    public RegexTransformer(String fileRegex, String regexMatch, String replacementValue, boolean replaceFirst, String encoding) {
        this.fileRegex = fileRegex;
        this.regexMatch = regexMatch;
        this.replacementValue = replacementValue;
        this.replaceFirst = replaceFirst;
        this.encoding = encoding;
    }

    @Override
    public boolean appliesTo(String filename) {
        return filename.matches(fileRegex);
    }

    @Override
    public byte[] transform(String entryPath, @Nullable byte[] data) throws Exception {
        String str = new String(data, encoding);
        if (replaceFirst) {
            str = str.replaceFirst(regexMatch, replacementValue);
        } else {
            str = str.replaceAll(regexMatch, replacementValue);
        }
        return str.getBytes(encoding);
    }

    @Override
    public @NotNull List<String> getNewFiles() {
        return Collections.emptyList();
    }

    public String getFileRegex() {
        return fileRegex;
    }

    public void setFileRegex(String fileRegex) {
        this.fileRegex = fileRegex;
    }

    public String getRegexMatch() {
        return regexMatch;
    }

    public void setRegexMatch(String regexMatch) {
        this.regexMatch = regexMatch;
    }

    public String getReplacementValue() {
        return replacementValue;
    }

    public void setReplacementValue(String replacementValue) {
        this.replacementValue = replacementValue;
    }

    public boolean isReplaceFirst() {
        return replaceFirst;
    }

    public void setReplaceFirst(boolean replaceFirst) {
        this.replaceFirst = replaceFirst;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
