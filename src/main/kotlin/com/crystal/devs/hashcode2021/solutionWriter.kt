package com.crystal.devs.hashcode2021

import java.io.File

fun writeSolution(solution: Solution, inputName: String) {
    File("solution_$inputName.txt").printWriter().use { out ->
        out.println(solution.projects.size)
        solution.projects.forEach { project ->
            out.println(project.name)
            out.println(project.contributors.joinToString(" "))
        }
    }
}