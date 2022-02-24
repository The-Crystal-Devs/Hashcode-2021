package com.crystal.devs.hashcode2021

fun computeSolution(parsedInput: ParsedInput): Solution {
    val maxPossibleDays = parsedInput.projects.maxBy { it.bestBeforeTime + it.score }

    val weightedProjects = parsedInput.projects.associateWith { it.computeWeight() }
    val sortedProjects = weightedProjects.entries.sortedBy { it.value }.toMutableList()
    val availableContributors = parsedInput.contributors.toMutableSet()
    val finishedProjects = mutableListOf<FinishedProject>()

    while(sortedProjects.isNotEmpty()) {
        val project = sortedProjects.removeAt(0).key
        val filledProject = associateContributorsToSkill(project.roles, availableContributors)

        if(filledProject.isNotEmpty()) {

            val finishedProject = FinishedProject(
                project.projectName,
                project.roles.map { role -> filledProject.find { it.first == role }!!.second })
            finishedProjects.add(finishedProject)

            filledProject.forEach {
                val contributor = availableContributors.find { contributor -> contributor.name == it.second }
                if(it.first.level == contributor!!.skills[it.first.name]) {
                    contributor.skills[it.first.name] = contributor.skills[it.first.name]!! + 1
                    contributor.skills.compute(it.first.name) { k, v -> (v ?: 0) + 1 }
                }
            }

            availableContributors.removeIf { filledProject.map { p -> p.second }.contains(it.name) }
        }


//        val possibleMentors = availableContributors.filter { contributor ->
//            skillToPossibleContributor.entries.count {
//                it.value.contains(contributor)
//            } > 1
//        }
//
//        val skillToPossibleMentored = project.key.roles.map { role ->
//            val possibleMentored = availableContributors.filter { ((it.skills[role.key] ?: 0) + 1) == role.value }
//                .filter { possibleMentors.find {  } }
//
//            role.key to possibleMentored
//        }
    }

    return Solution(finishedProjects)
}

fun associateContributorsToSkill(roles: List<Skill>, availableContributors: Set<Contributor>): List<Pair<Skill, String>> {
    val skillToPossibleContributor = roles.associateWith { role ->
        val possibleContributors = availableContributors.filter { (it.skills[role.name] ?: 0) >= role.level }
        possibleContributors
    }

    if (skillToPossibleContributor.any { it.value.isEmpty() }) {
        return emptyList()
    }

    val skillWithContributorsSortedByNbContributors = skillToPossibleContributor.entries.sortedByDescending { it.value.size }
    val chosenSkill = skillWithContributorsSortedByNbContributors.first()
    val remainingRoles = roles.filter { it != chosenSkill.key }
    for (i in 0 until chosenSkill.value.size) {
        val chosenContributor = chosenSkill.value[i]
        if(roles.size == 1) {
            return listOf((chosenSkill.key to chosenContributor.name))
        }

        val possibleAssociations = associateContributorsToSkill(
            remainingRoles,
            availableContributors.filter { it != chosenContributor }.toSet()
        )
        if(possibleAssociations.isNotEmpty()) {
            return possibleAssociations + (chosenSkill.key to chosenContributor.name)
        }
    }

    return emptyList()
}



data class ParsedInput(val contributors: List<Contributor>, val projects: List<Project>)
data class Contributor(val name: String, val skills: MutableMap<String , Int>)
data class Skill(val name: String, val level: Int)
data class Project(val projectName: String, val duration: Int, val score: Int, val bestBeforeTime: Int, val roles: List<Skill>) {
    fun computeWeight() : Double {
        return score.toDouble() / duration.toDouble()
    }
};

data class FinishedProject(val name: String, val contributors: List<String>)
data class Solution(val projects: List<FinishedProject>) {
    fun computeScore() = 1;
}