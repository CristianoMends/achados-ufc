import { User } from "src/users/entities/user.entity";
import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from "typeorm"

@Entity()
export class Item {

  @PrimaryGeneratedColumn()
  id: number;
  @Column()
  title: String;
  @Column()
  description: String;
  @Column()
  imageUrl: String;
  @Column()
  location: String;
  @CreateDateColumn()
  date: String;
  @Column({ default: false })
  isFound: Boolean = false;
  @ManyToOne(() => User, user => user.items, { eager: true, onDelete: 'CASCADE' })
  user: User
  item: Date;
}
