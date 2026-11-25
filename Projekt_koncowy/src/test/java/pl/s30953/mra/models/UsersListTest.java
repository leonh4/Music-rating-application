package pl.s30953.mra.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UsersListTest {

    /**
     * Tworzy wydanie o podanym identyfikatorze.
     */
    private static Release releaseWithId(int id) {
        Release release = new Release();
        release.setId(id);
        return release;
    }

    /**
     * Dodanie tego samego wydania dwa razy (także jako inna instancja) nie tworzy duplikatu.
     */
    @Test
    void addReleaseToListPreventsDuplicates() {
        UsersList list = new UsersList();

        list.addReleaseToList(releaseWithId(1));
        list.addReleaseToList(releaseWithId(1));

        assertThat(list.getReleases()).hasSize(1);
    }

    /**
     * Dodanie wielu wydań naraz pomija te, które już są na liście.
     */
    @Test
    void addReleasesToListSkipsExistingOnes() {
        UsersList list = new UsersList();
        list.addReleaseToList(releaseWithId(1));

        list.addReleasesToList(List.of(releaseWithId(1), releaseWithId(2), releaseWithId(3)));

        assertThat(list.getReleases()).extracting(Release::getId).containsExactly(1, 2, 3);
    }

    /**
     * Usunięcie pojedynczego wydania działa po identyfikatorze.
     */
    @Test
    void removeReleaseFromList() {
        UsersList list = new UsersList();
        list.addReleasesToList(List.of(releaseWithId(1), releaseWithId(2)));

        list.removeReleaseFromList(releaseWithId(1));

        assertThat(list.getReleases()).extracting(Release::getId).containsExactly(2);
    }

    /**
     * Usunięcie wielu wydań naraz zostawia tylko pozostałe.
     */
    @Test
    void removeReleasesFromList() {
        UsersList list = new UsersList();
        list.addReleasesToList(List.of(releaseWithId(1), releaseWithId(2), releaseWithId(3)));

        list.removeReleasesFromList(List.of(releaseWithId(1), releaseWithId(3)));

        assertThat(list.getReleases()).extracting(Release::getId).containsExactly(2);
    }
}
