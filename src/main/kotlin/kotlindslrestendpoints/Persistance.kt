package kotlindslrestendpoints

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
data class Orders(
        @Id @GeneratedValue
        val id: Long = 0,

        @get: NotBlank
        val detail: String = ""
)

@Repository
interface OrderRepository : JpaRepository<Orders, Long>