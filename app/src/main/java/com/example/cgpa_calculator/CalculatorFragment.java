package com.example.cgpa_calculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalculatorFragment extends Fragment implements SubjectAdapter.OnSubjectChangeListener {

    private TextView tvGpaDisplay;
    private TextView tvPercentageDisplay;
    private TextView tvTotalCreditsDisplay;
    private TextView tvRatingDisplay;

    private RecyclerView rvSubjects;
    private MaterialButton btnAddSubject;
    private MaterialButton btnCalculate;
    private MaterialButton btnSaveDatabase;

    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;
    private DatabaseHelper databaseHelper;

    private double currentGpa = 0.0;
    private double currentTotalCredits = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        tvGpaDisplay = view.findViewById(R.id.tvGpaDisplay);
        tvPercentageDisplay = view.findViewById(R.id.tvPercentageDisplay);
        tvTotalCreditsDisplay = view.findViewById(R.id.tvTotalCreditsDisplay);
        tvRatingDisplay = view.findViewById(R.id.tvRatingDisplay);

        rvSubjects = view.findViewById(R.id.rvSubjects);
        btnAddSubject = view.findViewById(R.id.btnAddSubject);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        btnSaveDatabase = view.findViewById(R.id.btnSaveDatabase);

        subjectList = new ArrayList<>();
        // Pre-populate with empty subject names so hints are shown instead of pre-filled text
        subjectList.add(new Subject("", 4.0, "O (10.0)", 10.0));
        subjectList.add(new Subject("", 4.0, "A+ (9.5)", 9.5));
        subjectList.add(new Subject("", 3.0, "A (9.0)", 9.0));
        subjectList.add(new Subject("", 3.0, "B+ (8.0)", 8.0));

        subjectAdapter = new SubjectAdapter(requireContext(), subjectList, this);
        rvSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSubjects.setAdapter(subjectAdapter);

        btnAddSubject.setOnClickListener(v -> {
            Subject newSubject = new Subject("", 3.0, "A+ (9.5)", 9.5);
            subjectList.add(newSubject);
            subjectAdapter.notifyItemInserted(subjectList.size() - 1);
            rvSubjects.smoothScrollToPosition(subjectList.size() - 1);
            calculateGpaAndUpdateUi();
        });

        btnCalculate.setOnClickListener(v -> {
            calculateGpaAndUpdateUi();
            Toast.makeText(requireContext(), "GPA Calculated Successfully!", Toast.LENGTH_SHORT).show();
        });

        btnSaveDatabase.setOnClickListener(v -> showSaveSemesterDialog());

        calculateGpaAndUpdateUi();

        return view;
    }

    @Override
    public void onSubjectChanged() {
        calculateGpaAndUpdateUi();
    }

    private void calculateGpaAndUpdateUi() {
        currentGpa = GpaCalculator.calculateGpa(subjectList);
        currentTotalCredits = GpaCalculator.calculateTotalCredits(subjectList);
        double percentage = GpaCalculator.convertGpaToPercentage(currentGpa);
        String rating = GpaCalculator.getPerformanceRating(currentGpa);

        tvGpaDisplay.setText(String.format(Locale.getDefault(), "%.2f", currentGpa));
        tvPercentageDisplay.setText(String.format(Locale.getDefault(), "Equivalent: %.1f%%", percentage));
        tvTotalCreditsDisplay.setText(String.format(Locale.getDefault(), "Total Credits: %.1f", currentTotalCredits));
        tvRatingDisplay.setText(rating);
    }

    private void showSaveSemesterDialog() {
        if (subjectList.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one subject to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentTotalCredits <= 0) {
            Toast.makeText(requireContext(), "Total credits must be greater than 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_save_semester, null);
        EditText etSemesterName = dialogView.findViewById(R.id.etSemesterName);

        int currentCount = databaseHelper.getAllSemesters().size();
        etSemesterName.setText("Semester " + (currentCount + 1));

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = etSemesterName.getText().toString().trim();
                    if (name.isEmpty()) {
                        name = "Semester " + (currentCount + 1);
                    }

                    List<Subject> subjectsToSave = new ArrayList<>();
                    for (int i = 0; i < subjectList.size(); i++) {
                        Subject s = subjectList.get(i);
                        String subName = s.getSubjectName() != null ? s.getSubjectName().trim() : "";
                        if (subName.isEmpty()) {
                            subName = "Course " + (i + 1);
                        }
                        subjectsToSave.add(new Subject(subName, s.getCredits(), s.getGrade(), s.getGradePoint()));
                    }

                    Semester semester = new Semester(name, currentGpa, currentTotalCredits, subjectsToSave);
                    long id = databaseHelper.saveSemester(semester);

                    if (id != -1) {
                        Toast.makeText(requireContext(), "Saved '" + name + "' to SQLite Database! 💾", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Error saving to database!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}