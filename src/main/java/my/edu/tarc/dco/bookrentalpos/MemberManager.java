package my.edu.tarc.dco.bookrentalpos;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to load Member data for the POS system
 *
 * @author Looz
 * @version 1.0
 */
public class MemberManager extends Manager<Member> {

    private Member[] memberList;
    private int memberCount;
    private DBManager db;
    private final int ARRAY_SIZE = 100;

    public MemberManager(DBManager db) {
        memberList = new Member[ARRAY_SIZE];
        this.db = db;
        reload();
    }

    /**
     * Reload all the data from database
     */
    @Override
    public void reload() {
        memberCount = 0;
        String sql = "SELECT * FROM member;";
        try {
            java.sql.ResultSet rs = db.resultQuery(sql);
            while (rs.next()) {
                Member s = new Member(rs.getInt("id"), rs.getString("date"), rs.getString("name"),
                        rs.getString("phoneNo"), rs.getString("email"), rs.getString("IC"), rs.getInt("points"));
                memberList[memberCount++] = s;
            }
        } catch (java.sql.SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    /**
     * Get reference to member object with specified ID
     *
     * @return Member object reference, null if no such member with this id
     */
    @Override
    public Member getById(int memID) {
        for (int i = 0; i < memberCount; i++) {
            if (memberList[i].getId() == memID) {
                return memberList[i];
            }
        }
        return null;
    }

    /**
     * Get reference to member object with specified Name
     *
     * @return Member object reference
     */
    @Override
    public Member getByName(String name) {
        for (int i = 0; i < memberCount; i++) {
            if (memberList[i].getName().equals(name)) {
                return memberList[i];
            }
        }
        return null;
    }

    /**
     * Get a copy of the Member List cache located in this instance
     * For now, you could obtain the length of the array through getMemberCount()
     *
     * @return A copy of Member array with 100 size pre-allocated
     * @see MemberManager#getMemberCount()
     */
    @Override
    public Member[] getCache() {
        return this.memberList.clone();
    }

    /**
     * Register a new member to database
     *
     * @param mem Member object without ID
     * @return True if member is successfully registered
     * @see Member#Member(java.lang.String, java.lang.String)
     */
    @Override
    public boolean add(Member mem) {
        String sql = String.format("INSERT INTO member(name, phoneNo, email, IC, points) VALUES('%s', '%s', '%s', '%s', %d)",
                mem.getName(), mem.getPhoneNo(), mem.getEmail(), mem.getIcNo(), mem.getMemberPoints());
        if (db.updateQuery(sql) == 1) {
            try {
                // id and date is generated by sqlite, i need to make a copy of it and store it
                // in my preloaded database
                // this query basically get the latest Mmeber entry inserted into database
                ResultSet rs = db.resultQuery(
                        "SELECT id, date FROM member WHERE id = (SELECT seq FROM sqlite_sequence WHERE name='member')");
                mem.setID(rs.getInt("id"));
                mem.setDateCreated(rs.getString("date"));

                // store in my preloaded database
                memberList[memberCount++] = mem;
            } catch (SQLException err) {
                System.out.println(err.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update data of existing member to database
     *
     * @param mem Member Object with ID, expecting a Member object reference
     *            instead of new Member object
     * @return True if the member's data is updated successfully
     */
    @Override
    public boolean update(Member mem) {
        if (mem.getId() == 0) {
            return false;
        }
        String sql = String.format("UPDATE member\n"
                + "SET name='%s', phoneNo='%s', email='%s',IC='%s', points=%d\n"
                + "WHERE id=%d;", mem.getName(), mem.getPhoneNo(), mem.getEmail(), mem.getIcNo(), mem.getMemberPoints(), mem.getId());
        if (db.updateQuery(sql) == 1) {
            return true;
        }
        return false;
    }

    /**
     * Remove a member from database<br>
     * NOTE: The removed member's data will be removed from other related table
     * as well
     *
     * @param memID MemberID which to be removed
     * @return true if member was removed successfully
     */
    @Override
    public boolean remove(int memID) {
        db.execQuery("UPDATE book\n"
                + "SET lastRentedBy=NULL\n"
                + "WHERE lastRentedBy=" + memID);
        db.execQuery("UPDATE book\n"
                + "SET lastReservedBy=NULL, isReserved=0\n"
                + "WHERE lastReservedBy=" + memID);
        db.execQuery("UPDATE transactions\n"
                + "SET memberInvolved=NULL\n"
                + "WHERE memberInvolved=" + memID);
        String sql = String.format("DELETE FROM member WHERE id=%d", memID);
        Member[] tmpList = new Member[ARRAY_SIZE];

        // [1,23,4,5,6]
        // [1, 23, 5, 6]
        if (db.updateQuery(sql) == 1) {
            int b = 0;
            for (int a = 0; a < memberCount; a++) {
                if (memberList[a].getId() != memID) {
                    tmpList[b++] = memberList[a];
                }
            }
            memberList = tmpList.clone();
            memberCount--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return Member count loaded into the memberList array
     */
    public int getMemberCount() {
        return this.memberCount;
    }

}
