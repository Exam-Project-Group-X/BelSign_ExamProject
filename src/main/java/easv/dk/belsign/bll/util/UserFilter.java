package easv.dk.belsign.bll.util;

import easv.dk.belsign.be.User;

import java.util.ArrayList;
import java.util.List;

public class UserFilter {

    /**
     * Filters by search text and selected role.
     */
    public List<User> filter(List<User> users, String search, String selectedRole) {
        String userSearch = (search == null ? "" : search.trim()).toLowerCase();
        String filterRole = (selectedRole == null ? "all roles" : selectedRole).toLowerCase();

        List<User> result = new ArrayList<>();
        for (User u : users) {
            if (matchesSearch(u, userSearch) && matchesFilterRole(u, filterRole)) {
                result.add(u);
            }
        }
        return result;
    }

    private boolean matchesSearch(User user, String search) {
        String fullName = user.getFullName() == null ? "" : user.getFullName().toLowerCase();
        String email = user.getEmail() == null ? "" : user.getEmail().toLowerCase();
        String role = user.getRoleName() == null ? "" : user.getRoleName().toLowerCase();

        return fullName.contains(search)
                || email.contains(search)
                || role.contains(search);
    }

    private boolean matchesFilterRole(User user, String normRole) {
        if ("all roles".equals(normRole)) {
            return true;
        }
        String role = user.getRoleName() == null ? "" : user.getRoleName().toLowerCase();
        return role.equals(normRole);
    }
}
