package com.example.cgpa_calculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class HistoryFragment extends Fragment implements HistoryAdapter.OnDeleteClickListener {

    private TextView tvCgpaDisplay;
    private TextView tvTotalCreditsHistory;
    private TextView tvTotalCoursesHistory;
    private LinearLayout layoutEmptyState;
    private RecyclerView rvHistory;
    private MaterialButton btnClearHistory;

    private DatabaseHelper databaseHelper;
    private HistoryAdapter historyAdapter;
    private List<Semester> semesterList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        tvCgpaDisplay = view.findViewById(R.id.tvCgpaDisplay);
        tvTotalCreditsHistory = view.findViewById(R.id.tvTotalCreditsHistory);
        tvTotalCoursesHistory = view.findViewById(R.id.tvTotalCoursesHistory);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        rvHistory = view.findViewById(R.id.rvHistory);
        btnClearHistory = view.findViewById(R.id.btnClearHistory);

        semesterList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(requireContext(), semesterList, this);
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(historyAdapter);

        btnClearHistory.setOnClickListener(v -> confirmClearAllHistory());

        loadHistoryFromDatabase();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistoryFromDatabase();
    }

    private void loadHistoryFromDatabase() {
        if (databaseHelper == null) return;

        semesterList.clear();
        semesterList.addAll(databaseHelper.getAllSemesters());
        historyAdapter.notifyDataSetChanged();

        double cgpa = databaseHelper.calculateOverallCGPA();
        double totalCredits = databaseHelper.getTotalCreditsAllSemesters();
        int totalCourses = databaseHelper.getTotalCoursesAllSemesters();

        tvCgpaDisplay.setText(String.format(Locale.getDefault(), "%.2f", cgpa));
        tvTotalCreditsHistory.setText(String.format(Locale.getDefault(), "Total Credits: %.1f", totalCredits));
        tvTotalCoursesHistory.setText(String.format(Locale.getDefault(), "Completed Courses: %d", totalCourses));

        if (semesterList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            btnClearHistory.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            btnClearHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteClick(Semester semester, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Semester Record")
                .setMessage("Are you sure you want to delete '" + semester.getSemesterName() + "' from the database?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = databaseHelper.deleteSemester(semester.getId());
                    if (success) {
                        Toast.makeText(requireContext(), "Deleted '" + semester.getSemesterName() + "'", Toast.LENGTH_SHORT).show();
                        loadHistoryFromDatabase();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete record!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmClearAllHistory() {
        if (semesterList.isEmpty()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Clear Database History")
                .setMessage("Are you sure you want to delete ALL saved semester records from SQLite database? This action cannot be undone.")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    databaseHelper.clearAllHistory();
                    Toast.makeText(requireContext(), "Database cleared!", Toast.LENGTH_SHORT).show();
                    loadHistoryFromDatabase();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}