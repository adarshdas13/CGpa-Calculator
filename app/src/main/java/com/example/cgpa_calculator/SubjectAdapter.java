package com.example.cgpa_calculator;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    public interface OnSubjectChangeListener {
        void onSubjectChanged();
    }

    private final Context context;
    private final List<Subject> subjectList;
    private final OnSubjectChangeListener changeListener;
    private final String[] gradeOptions;

    public SubjectAdapter(Context context, List<Subject> subjectList, OnSubjectChangeListener changeListener) {
        this.context = context;
        this.subjectList = subjectList;
        this.changeListener = changeListener;
        this.gradeOptions = GpaCalculator.getGradeOptions();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.bind(subject, position);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSubjectNumber;
        private final EditText etSubjectName;
        private final EditText etCredits;
        private final Spinner spinnerGrade;
        private final ImageButton btnDeleteSubject;

        private TextWatcher nameWatcher;
        private TextWatcher creditsWatcher;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectNumber = itemView.findViewById(R.id.tvSubjectNumber);
            etSubjectName = itemView.findViewById(R.id.etSubjectName);
            etCredits = itemView.findViewById(R.id.etCredits);
            spinnerGrade = itemView.findViewById(R.id.spinnerGrade);
            btnDeleteSubject = itemView.findViewById(R.id.btnDeleteSubject);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    context, android.R.layout.simple_spinner_item, gradeOptions);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGrade.setAdapter(spinnerAdapter);
        }

        public void bind(Subject subject, int position) {
            tvSubjectNumber.setText(context.getString(R.string.total_subjects_label) + " " + (position + 1));

            if (nameWatcher != null) etSubjectName.removeTextChangedListener(nameWatcher);
            if (creditsWatcher != null) etCredits.removeTextChangedListener(creditsWatcher);

            etSubjectName.setText(subject.getSubjectName());
            etCredits.setText(subject.getCredits() > 0 ? String.valueOf(subject.getCredits()) : "");

            int spinnerIndex = 0;
            for (int i = 0; i < gradeOptions.length; i++) {
                if (gradeOptions[i].equalsIgnoreCase(subject.getGrade()) ||
                        gradeOptions[i].startsWith(subject.getGrade())) {
                    spinnerIndex = i;
                    break;
                }
            }
            spinnerGrade.setSelection(spinnerIndex);

            nameWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < subjectList.size()) {
                        subjectList.get(pos).setSubjectName(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };
            etSubjectName.addTextChangedListener(nameWatcher);

            creditsWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < subjectList.size()) {
                        try {
                            double cr = Double.parseDouble(s.toString());
                            subjectList.get(pos).setCredits(cr);
                        } catch (NumberFormatException e) {
                            subjectList.get(pos).setCredits(0.0);
                        }
                        if (changeListener != null) {
                            changeListener.onSubjectChanged();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };
            etCredits.addTextChangedListener(creditsWatcher);

            spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    int adapterPos = getBindingAdapterPosition();
                    if (adapterPos != RecyclerView.NO_POSITION && adapterPos < subjectList.size()) {
                        String selectedGrade = gradeOptions[pos];
                        subjectList.get(adapterPos).setGrade(selectedGrade);
                        if (changeListener != null) {
                            changeListener.onSubjectChanged();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            btnDeleteSubject.setOnClickListener(v -> {
                int adapterPos = getBindingAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION && adapterPos < subjectList.size()) {
                    subjectList.remove(adapterPos);
                    notifyItemRemoved(adapterPos);
                    notifyItemRangeChanged(adapterPos, subjectList.size());
                    if (changeListener != null) {
                        changeListener.onSubjectChanged();
                    }
                }
            });
        }
    }
}