import { Injectable, HttpException, HttpStatus } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './entities/user.entity';
import * as bcrypt from 'bcryptjs';
import { ViewUserDto } from './dto/view-user.dto';

@Injectable()
export class UsersService {

  constructor(
    @InjectRepository(User) private repo: Repository<User>
  ) { }

  async create(createUserDto: CreateUserDto) {
    try {
      const user = this.repo.create(createUserDto);
      user.password = await bcrypt.hash(String(user.password), 10);
      await this.repo.save(user);
    } catch (error) {
      throw new HttpException('Erro ao buscar os registros About', HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async findAll() {
    try {
      const users = await this.repo.find();
      return users.map(user => new ViewUserDto(user));
    } catch (error) {
      throw new HttpException('Erro ao buscar os registros About', HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  async findOne(username: string) {
    try {
      const user = await this.repo.findOne({ where: { username } });
      if (!user) {
        throw new HttpException('Usuário não encontrado', HttpStatus.NOT_FOUND);
      }
      return new ViewUserDto(user);
    } catch (error) {
      throw new HttpException('Erro ao buscar usuario', HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  update(id: number, updateUserDto: UpdateUserDto) {
    return `This action updates a #${id} user`;
  }

  remove(id: number) {
    return `This action removes a #${id} user`;
  }
}
