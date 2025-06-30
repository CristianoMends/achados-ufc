import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseInterceptors,
  UploadedFile,
  UseGuards,
  Req,
  ParseIntPipe,
  Request as request
} from '@nestjs/common';
import { ItemService } from './item.service';
import { CreateItemDto } from './dto/create-item.dto';
import { FileInterceptor } from '@nestjs/platform-express';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { Request } from 'express';
import { Item } from './entities/item.entity';

@Controller('items')
export class ItemsController {
  constructor(private readonly itemsService: ItemService,
  ) { }


  @Post()
  @UseGuards(JwtAuthGuard)
  @UseInterceptors(FileInterceptor('file')) // Campo 'file' no multipart/form-data
  async create(
    @UploadedFile() file: Express.Multer.File,
    @Body() createItemDto: CreateItemDto,
    @Req() req: Request,
  ): Promise<Item> {
    const userId = req.user?.['id'];
    if (!userId) {
      throw new Error('User ID not found in request');
    }
    return await this.itemsService.create(createItemDto, file, userId);
  }

  @Get()
  findAll() {
    return this.itemsService.findAll();
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.itemsService.remove(+id);
  }
}
