package com.crystal.devs.hashcode2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File


internal class HashCodeRunnerTest {
    @Test
    fun `run Input A`() {
        val input = File("./src/main/resources/a_an_example.in.txt").readLines(Charsets.UTF_8)
        assertThat(runInput(input, "A")).isGreaterThan(0)
    }

    @Test
    fun `run Input B`() {
        val input = File("./src/main/resources/b_better_start_small.in.txt").readLines(Charsets.UTF_8)
        assertThat(runInput(input, "B")).isGreaterThan(0)
    }

    @Test
    fun `run Input C`() {
        val input = File("./src/main/resources/c_collaboration.in.txt").readLines(Charsets.UTF_8)
        assertThat(runInput(input, "C")).isGreaterThan(0)
    }


    @Test
    fun `run Input D`() {
        val input = File("./src/main/resources/d_dense_schedule.in.txt").readLines(Charsets.UTF_8)
        assertThat(runInput(input, "D")).isGreaterThan(0)
    }


    @Test
    fun `run Input E`() {
        val input = File("./src/main/resources/e_exceptional_skills.in.txt").readLines(Charsets.UTF_8)
        assertThat(runInput(input, "E")).isGreaterThan(0)
    }
}

