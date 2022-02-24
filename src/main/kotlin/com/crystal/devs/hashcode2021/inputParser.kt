package com.crystal.devs.hashcode2021

fun parseInput(input: List<String>): ParsedInput {
    val (nbContributors, nbProjects) = input.first().split(" ").map { it.toInt() }
    val contributors = mutableListOf<Contributor>()
    val drop = input.drop(1)
    var i = 0
    while (contributors.size < nbContributors) {
        val (name, skillNumber) = drop[i].split(" ")
        val skills = parseSkill(skillNumber.toInt(), drop, i)

        contributors.add(Contributor(name, skills))
        i += 1 + skillNumber.toInt()
    }

    val projectsInput = input.drop(1 + i)
    val projects = mutableListOf<Project>()
    i = 0
    while (projects.size < nbProjects) {
        val split = projectsInput[i].split(" ")
        val projectName = split[0]
        val (duration, score, bestBefore, nbSkils) = split.drop(1).map { it.toInt() }
        val skills = parseSkill(nbSkils, projectsInput, i)

        projects.add(Project(projectName, duration, score, bestBefore, skills))
        i += 1 + nbSkils
    }

    return ParsedInput(contributors, projects)
}

private fun parseSkill(nbSkils: Int, projectsInput: List<String>, i: Int): Map<String, Int> {
    val skills = (1..nbSkils).associate {
        val (skillName, skillLevel) = projectsInput[i + it].split(" ")
        skillName to skillLevel.toInt()
    }
    return skills
}

data class ParsedInput(val contributors: List<Contributor>, val projects: List<Project>)

data class Contributor(val name: String, val skills: Map<String, Int>)
data class Project(val projectName: String, val duration: Int, val score: Int, val bestBeforeTime: Int, val roles: Map<String, Int>)
