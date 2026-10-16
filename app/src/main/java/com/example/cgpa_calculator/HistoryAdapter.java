package com.example.cgpa_calculator;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Semester semester, int position);
    }

    private final Context context;
    private final List<Semester> semesterList;
    private final OnDeleteClickListener deleteClickListener;

    public HistoryAdapter(Context context, List<Semester> semesterList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.semesterList = semesterList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Semester semester = semesterList.get(position);
        holder.bind(semester, position);
    }

    @Override
    public int getItemCount() {
        return semesterList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSemesterName;
        private final TextView tvDateSaved;
        private final TextView tvGpaValue;
        private final TextView tvTotalCredits;
        private final TextView tvSubjectCount;
        private final ImageButton btnDeleteSemester;
        private final LinearLayout layoutSubjectsContainer;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvDateSaved = itemView.findViewById(R.id.tvDateSaved);
            tvGpaValue = itemView.findViewById(R.id.tvGpaValue);
            tvTotalCredits = itemView.findViewById(R.id.tvTotalCredits);
            tvSubjectCount = itemView.findViewById(R.id.tvSubjectCount);
            btnDeleteSemester = itemView.findViewById(R.id.btnDeleteSemester);
            layoutSubjectsContainer = itemView.findViewById(R.id.layoutSubjectsContainer);
        }

        public void bind(Semester semester, int position) {
            tvSemesterName.setText(semester.getSemesterName());
            tvGpaValue.setText(String.format(Locale.getDefault(), "%.2f", semester.getGpa()));
            tvTotalCredits.setText(String.format(Locale.getDefault(), "Total Credits: %.1f", semester.getTotalCredits()));

            int subjectCount = semester.getSubjectList() != null ? semester.getSubjectList().size() : 0;
            tvSubjectCount.setText(String.format(Locale.getDefault(), "%d Course%s", subjectCount, subjectCount == 1 ? "" : "s"));

            CharSequence dateFormatted = DateFormat.format("MMM dd, yyyy - hh:mm a", new Date(semester.getTimestamp()));
            tvDateSaved.setText(dateFormatted);

            layoutSubjectsContainer.removeAllViews();
            if (semester.getSubjectList() != null && !semester.getSubjectList().isEmpty()) {
                for (Subject subject : semester.getSubjectList()) {
                    View subView = LayoutInflater.from(context).inflate(R.layout.item_history_subject, layoutSubjectsContainer, false);
                    TextView tvSubName = subView.findViewById(R.id.tvHistorySubjectName);
                    TextView tvSubCredits = subView.findViewById(R.id.tvHistoryCredits);
                    TextView tvSubGrade = subView.findViewById(R.id.tvHistoryGrade);

                    String name = subject.getSubjectName();
                    if (name == null || name.trim().isEmpty()) {
                        name = "Course Subject";
                    }
                    tvSubName.setText(name);
                    tvSubCredits.setText(String.format(Locale.getDefault(), "%.1f Credits", subject.getCredits()));
                    tvSubGrade.setText(subject.getGrade());

                    layoutSubjectsContainer.addView(subView);
                }
            }

            btnDeleteSemester.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        deleteClickListener.onDeleteClick(semester, pos);
                    }
                }
            });
        }
    }
}