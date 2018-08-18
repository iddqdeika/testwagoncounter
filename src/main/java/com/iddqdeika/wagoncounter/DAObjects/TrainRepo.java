package com.iddqdeika.wagoncounter.DAObjects;

import com.iddqdeika.wagoncounter.entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepo extends JpaRepository<Train, Integer>{
}
