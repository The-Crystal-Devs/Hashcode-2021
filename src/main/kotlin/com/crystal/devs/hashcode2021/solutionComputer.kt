package com.crystal.devs.hashcode2021

fun computeSolution(parsedInput: ParsedInput): Solution {
    return Solution(emptyList())
}

data class Solution(val projects: List<ProjectOut>) {
    fun computeScore() = 1;
}

data class ProjectOut(val name: String, val contributors: List<String>)