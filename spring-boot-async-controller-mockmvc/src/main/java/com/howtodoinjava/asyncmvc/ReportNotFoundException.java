package com.howtodoinjava.asyncmvc;
public class ReportNotFoundException extends RuntimeException { public ReportNotFoundException(String month) { super("No report for " + month); } }
