package com.crystal.devs.hashcode2021

fun parseInput(input: List<String>): ParsedInput {
    val (nbContributors, nbProjects) = input.first().split(" ").map { it.toInt() }
    val contributors = mutableListOf<Contributor>()
    val drop = input.drop(1)
    var i = 0
    while (contributors.size < nbContributors) {
        val (name, skillNumber) = drop[i].split(" ")
        val skills = parseSkillMap(skillNumber.toInt(), drop, i)

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

private fun parseSkill(nbSkils: Int, projectsInput: List<String>, i: Int): List<Skill> {
    val skills = (1..nbSkils).map {
        val (skillName, skillLevel) = projectsInput[i + it].split(" ")
        Skill(skillName, skillLevel.toInt())
    }
    return skills
}


private fun parseSkillMap(nbSkils: Int, projectsInput: List<String>, i: Int): MutableMap<String, Int> {
    val skills = (1..nbSkils).associate {
        val (skillName, skillLevel) = projectsInput[i + it].split(" ")
        skillName to skillLevel.toInt()
    }
    return skills.toMutableMap()
}
