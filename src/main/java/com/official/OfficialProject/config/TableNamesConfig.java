package com.official.OfficialProject.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TableNamesConfig {
    private static String AdminTable = "MAdmin";
    private static String InstructorTable = "MInstructor";
    private static String StudentTable = "MStudent";
    private static String CourseTable = "MCourse";
    private static String ExamTypeTable = "MExamType";
    private static String QTypeTable = "MQuestionType";
    private static String UniversityTable = "MUniversity";
    private static String FacultyTable = "MFaculty";
    private static String MajorTable = "MMajor";
    private static String ExamInfoTable = "MExamInfo";
    private static String MCQTable = "QMultipleChoice";
    private static String TFQTable = "QTrueFalse";
    private static String MRQTable = "QMultipleResponse";
    private static String FBQTable = "QFillBlank";
    private static String NQTable = "QNumber";
    private static String MCQPoolTable = "PoolMultipleChoice";
    private static String TFQPoolTable = "PoolTrueFalse";
    private static String MRQPoolTable = "PoolMultipleResponse";
    private static String FBQPoolTable = "PoolFillBlank";
    private static String NQPoolTable = "PoolNumber";
    private static String MCQAnswerTable = "AMultipleChoice";
    private static String TFQAnswerTable = "ATrueFalse";
    private static String MRQAnswerTable = "AMultipleResponse";
    private static String FBQAnswerTable = "AFillBlank";
    private static String NQAnswerTable = "ANumber";
    private static String StuExamInfoTable = "StudentExamInfo";
    private static String ExamQTypeInfoTable = "ExamQTypeInfo";


    public static String getAdminTable() {
        return AdminTable;
    }

    public static String getInstructorTable() {
        return InstructorTable;
    }

    public static String getStudentTable() {
        return StudentTable;
    }

    public static String getCourseTable() {
        return CourseTable;
    }

    public static String getExamTypeTable() {
        return ExamTypeTable;
    }

    public static String getQTypeTable() {
        return QTypeTable;
    }

    public static String getUniversityTable() {
        return UniversityTable;
    }

    public static String getFacultyTable() {
        return FacultyTable;
    }

    public static String getMajorTable() {
        return MajorTable;
    }

    public static String getExamInfoTable() {
        return ExamInfoTable;
    }

    public static String getMCQTable() {
        return MCQTable;
    }

    public static String getTFQTable() {
        return TFQTable;
    }

    public static String getMRQTable() {
        return MRQTable;
    }

    public static String getFBQTable() {
        return FBQTable;
    }

    public static String getNQTable() {
        return NQTable;
    }

    public static String getMCQPoolTable() {
        return MCQPoolTable;
    }

    public static String getTFQPoolTable() {
        return TFQPoolTable;
    }

    public static String getMRQPoolTable() {
        return MRQPoolTable;
    }

    public static String getFBQPoolTable() {
        return FBQPoolTable;
    }

    public static String getNQPoolTable() {
        return NQPoolTable;
    }

    public static String getMCQAnswerTable() {
        return MCQAnswerTable;
    }

    public static String getTFQAnswerTable() {
        return TFQAnswerTable;
    }

    public static String getMRQAnswerTable() {
        return MRQAnswerTable;
    }

    public static String getFBQAnswerTable() {
        return FBQAnswerTable;
    }

    public static String getNQAnswerTable() {
        return NQAnswerTable;
    }

    public static String getStuExamInfoTable() {
        return StuExamInfoTable;
    }

    public static String getExamQTypeInfoTable() {
        return ExamQTypeInfoTable;
    }
}
