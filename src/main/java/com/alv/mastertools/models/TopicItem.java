package com.alv.mastertools.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TopicItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private List<TopicItem> children;
    private List<NoteData> notes;

    public TopicItem(String title) {
        this.title = title;
        this.children = new ArrayList<>();
        this.notes = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TopicItem> getChildren() {
        return children;
    }

    public void setChildren(List<TopicItem> children) {
        this.children = children;
    }

    public List<NoteData> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteData> notes) {
        this.notes = notes;
    }

    public void addChild(TopicItem item) {
        if (this.children == null)
            this.children = new ArrayList<>();
        this.children.add(item);
    }

    public void addNote(NoteData note) {
        if (this.notes == null)
            this.notes = new ArrayList<>();
        this.notes.add(note);
    }
}
