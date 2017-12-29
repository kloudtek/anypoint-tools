package com.kloudtek.anypoint.provision;

public abstract class Transformer {
    private Transformer next;

    public void addNext(Transformer transformer) {
        if (next != null) {
            next.addNext(transformer);
        } else {
            next = transformer;
        }
    }

    public final byte[] transform(byte[] data) throws Exception {
        byte[] converted = doTransform(data);
        if (next != null) {
            return next.transform(converted);
        } else {
            return converted;
        }
    }

    public abstract byte[] doTransform(byte[] data) throws Exception;
}
