package net.weichware.jbdao.ui.dialog;

public enum EditDialogMode {
    ADD("Add"),
    EDIT("Edit"),
    COPY("Copy");

    public final String text;

    EditDialogMode(String text) {
        this.text = text;
    }

}
