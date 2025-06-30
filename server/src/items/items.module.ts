import { Module } from '@nestjs/common';
import { ItemService } from './item.service';
import { ItemsController } from './items.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Item } from './entities/item.entity';
import { User } from 'src/users/entities/user.entity';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [TypeOrmModule.forFeature([Item, User]),
    ConfigModule,
  ],
  controllers: [ItemsController],
  providers: [ItemService],
})
export class ItemsModule { }
