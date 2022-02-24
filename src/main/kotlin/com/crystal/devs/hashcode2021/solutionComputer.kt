package com.crystal.devs.hashcode2021

fun computeSolution(parsedInput: ParsedInput): Solution {
    val weightedProjects = parsedInput.projects.associateWith { it.computeWeight() }
    val sortedProjects = weightedProjects.entries.sortedByDescending { it.value }.toMutableList()
    val availableContributors = parsedInput.contributors.toMutableSet()
    val finishedProjects = mutableListOf<FinishedProject>()
    var daySituation = DaySituation(
        42,
        mutableListOf(),
        availableContributors,
        sortedProjects,
        mutableListOf()
    )

    var i = 0
    while(daySituation.sortedRemainingProjects.isNotEmpty() && i < 1000) {
        daySituation = processDay(daySituation)

        // Level up
        val finishedProjectsDuringDay = daySituation.finishedProjects
//        finishedProjectsDuringDay.forEach {
//            val project = sortedProjects.find { project -> project.key.projectName == it.name }!!
//            daySituation.availableContributors
//        }

        finishedProjects.addAll(finishedProjectsDuringDay)
        ++i
    }

    return Solution(daySituation.finishedProjects)

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

fun processDay(daySituation: DaySituation): DaySituation {
    // Launch projects
    var i = 0
    val availableContributors = daySituation.availableContributors.toMutableList()
    val ongoingProjects = daySituation.sortedByEndDateOngoingProjects.toMutableList()
    val sortedRemainingProjects = daySituation.sortedRemainingProjects

    while (availableContributors.isNotEmpty() && i < daySituation.sortedRemainingProjects.size) {
        val processedProject = processProject(
            daySituation.dayNumber,
            daySituation.sortedRemainingProjects[i].key,
            availableContributors.toSet()
        )

        if (processedProject != null) {
            sortedRemainingProjects.remove( daySituation.sortedRemainingProjects[i])
            ongoingProjects.add(processedProject)
            val contributorsWorkingOnProject = processedProject.contributors.map{ c -> c.second}.toSet()
            availableContributors.removeAll(contributorsWorkingOnProject)
        }

        ++i
    }

    val sortedByEndDateOngoingProjects = ongoingProjects.sortedBy { it.endDay }
    if(sortedByEndDateOngoingProjects.isNotEmpty()) {
        val nextFinishedProject = sortedByEndDateOngoingProjects.first()
        val nextDay = nextFinishedProject.endDay
        val nextFinishedProjects = sortedByEndDateOngoingProjects.filter { it.endDay == nextDay }
        nextFinishedProjects.map { it.contributors }
            .forEach { availableContributors.addAll(it.map { it.second }) }
        val finishedProjects =
            nextFinishedProjects.map { FinishedProject(it.name, it.contributors.map { c -> c.second.name }) }
        return daySituation.copy(
            dayNumber = nextDay,
            sortedByEndDateOngoingProjects = sortedByEndDateOngoingProjects.filter { it.endDay < nextDay },
            availableContributors = availableContributors.toSet(),
            sortedRemainingProjects = sortedRemainingProjects,
            finishedProjects = finishedProjects
        )
    }


    return daySituation.copy(
        dayNumber = Int.MAX_VALUE,
        sortedByEndDateOngoingProjects = emptyList(),
        availableContributors = emptySet(),
        sortedRemainingProjects = mutableListOf()
    )

}

fun processProject(
    currentDay: Int,
    projectToLaunch: Project,
    availableContributors: Set<Contributor>
): ProcessedProject? {
    val filledProject = associateContributorsToSkill(projectToLaunch.roles, availableContributors)

    if (filledProject.isEmpty()) {
        return null
    }

    return ProcessedProject(
        projectToLaunch.projectName,
        currentDay + projectToLaunch.duration,
        filledProject
    )
}

data class ProcessedProject(val name: String, val endDay: Int, val contributors: List<Pair<Skill, Contributor>>)

data class DaySituation(
    val dayNumber: Int,
    val sortedByEndDateOngoingProjects: List<ProcessedProject>,
    val availableContributors: Set<Contributor>,
    val sortedRemainingProjects: MutableList<Map.Entry<Project, Double>>,
    val finishedProjects: List<FinishedProject>
)

fun associateContributorsToSkill(
    roles: List<Skill>,
    availableContributors: Set<Contributor>
): List<Pair<Skill, Contributor>> {
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
    val sortedContributors = chosenSkill.value.sortedBy { it.skills[chosenSkill.key.name] }
    for (chosenContributor in sortedContributors) {
        if(roles.size == 1) {
            return listOf((chosenSkill.key to chosenContributor))
        }

        val possibleAssociations = associateContributorsToSkill(
            remainingRoles,
            availableContributors.filter { it != chosenContributor }.toSet()
        )
        if(possibleAssociations.isNotEmpty()) {
            return possibleAssociations + (chosenSkill.key to chosenContributor)
        }
    }

    return emptyList()
}



data class ParsedInput(val contributors: List<Contributor>, val projects: List<Project>)
data class Contributor(val name: String, val skills: MutableMap<String , Int>)
data class Skill(val name: String, val level: Int, val index: Int)
data class Project(val projectName: String, val duration: Int, val score: Int, val bestBeforeTime: Int, val roles: List<Skill>) {
    fun computeWeight() : Double {
//        return bestBeforeTime.toDouble()
        return score.toDouble() / duration.toDouble() * (1.0 / computeDifficulty())
    }

    fun computeDifficulty() : Double {
        return roles.sumBy { it.level }.toDouble()
    }
};

data class FinishedProject(val name: String, val contributors: List<String>)
data class Solution(val projects: List<FinishedProject>) {
    fun computeScore() = 1;
}