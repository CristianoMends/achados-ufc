import {
  HttpException,
  HttpStatus,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Item } from './entities/item.entity';
import { CreateItemDto } from './dto/create-item.dto';
import { ConfigService } from '@nestjs/config';
import { put, del } from '@vercel/blob';
import { join } from 'path';
import { writeFile, mkdir, unlink } from 'fs/promises';
import { existsSync } from 'fs';
import { User } from 'src/users/entities/user.entity';
import { ViewItemDto } from './dto/view-item.dto';

@Injectable()
export class ItemService {
  constructor(
    @InjectRepository(Item)
    private itemRepository: Repository<Item>,
    @InjectRepository(User)
    private userRepository: Repository<User>,
    private configService: ConfigService,
  ) { }

  async create(createItemDto: CreateItemDto, image: Express.Multer.File, userId: number) {
    if (!image.mimetype.startsWith('image/')) {
      throw new HttpException(
        'Invalid file type. Only images are allowed.',
        HttpStatus.BAD_REQUEST,
      );
    }

    const maxSize = 3 * 1024 * 1024;
    if (image.size > maxSize) {
      throw new HttpException(
        'File too large. Maximum size is 3MB.',
        HttpStatus.BAD_REQUEST,
      );
    }

    const user = await this.userRepository.findOne({ where: { id: userId } });
    if (!user) {
      throw new NotFoundException(`User with ID ${userId} not found`);
    }

    const strategy = this.configService.get<string>('UPLOAD_STRATEGY');
    const item = this.itemRepository.create(createItemDto);

    if (strategy === 'vercel') {
      const blob = await put(`items/image.png`, image.buffer, {
        access: 'public',
        addRandomSuffix: true,
      });
      item.imageUrl = blob.url;
    } else {
      const folderPath = join(__dirname, '..', '..', 'uploads', 'items');
      if (!existsSync(folderPath)) {
        await mkdir(folderPath, { recursive: true });
      }

      const fileName = `image-${Date.now()}.png`;
      const filePath = join(folderPath, fileName);
      await writeFile(filePath, image.buffer);

      item.imageUrl = `http://localhost:3000/uploads/items/${fileName}`;
    }

    item.user = user;
    item.date = new Date().toISOString(); // Pode ser omitido se usar @CreateDateColumn

    return this.itemRepository.save(item);
  }

  async findAll(): Promise<ViewItemDto[]> {
    const items = await this.itemRepository.find({
      order: { date: 'DESC' },
    });

    return items.map(i => new ViewItemDto(i));
  }

  async remove(id: number): Promise<void> {
    const item = await this.itemRepository.findOne({ where: { id } });

    if (!item) {
      throw new NotFoundException(`Item with ID ${id} not found`);
    }

    const strategy = this.configService.get<string>('UPLOAD_STRATEGY');

    if (strategy === 'vercel') {
      await del(item.imageUrl.toString());
    } else {
      const imagePath = item.imageUrl.replace('http://localhost:3000/', '');
      const filePath = join(__dirname, '..', '..', imagePath);
      if (existsSync(filePath)) {
        await unlink(filePath);
      }
    }

    await this.itemRepository.remove(item);
  }
}
