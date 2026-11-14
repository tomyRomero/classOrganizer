package com.example.classorganizer;

/**
 * A student's account, as stored in usertable. A record is Java's short form for an immutable
 * data class, like a C# record: the constructor, getters, equals and hashCode come for free.
 */
record Customer(int id, String firstName, String lastName, String email, int age, String username,
                String passwordHash) {

    String fullName() {
        return firstName + " " + lastName;
    }

    /** Leaves the hash out, so printing or logging an account never writes it anywhere. */
    @Override
    public String toString() {
        return "Customer[id=" + id + ", username=" + username + "]";
    }
}
