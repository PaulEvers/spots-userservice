package nl.paulevers.spotsuserservice;

import nl.paulevers.spotsuserservice.entities.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Usertests {

    User user = new User();

    @Test
    void userLikes() {
        assertThat(user.getFavoriteSpotsIds().size()).isEqualTo(0);
        user.likeSpot("testId");
        assertThat(user.getFavoriteSpotsIds().size()).isEqualTo(1);
    }

    @Test
    void userDislikes() {
        assertThat(user.getFavoriteSpotsIds().size()).isEqualTo(0);
        user.likeSpot("testId2");
        assertThat(user.getFavoriteSpotsIds().size()).isEqualTo(1);
        user.dislikeSpot("testId2");
        assertThat(user.getFavoriteSpotsIds().size()).isEqualTo(0);
    }
}
